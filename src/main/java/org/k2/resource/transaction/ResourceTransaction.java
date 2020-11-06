package org.k2.resource.transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.location.DigestableLocation;
import org.k2.resource.location.DigestableResource;
import org.k2.resource.location.TxDigestableResource;
import org.k2.resource.transaction.exception.PreCommitException;
import org.k2.resource.transaction.exception.RollbackException;

public class ResourceTransaction implements Transaction {
	
	class TransactionItem {
		DigestableResource txResource;
		File backupFile;
	}
	
	private final DigestableLocation dir;
	private final ResourceTransactionManager manager;
	private final Map<TxDigestableResource, TransactionItem> resourcesMap = new HashMap<>();
	private boolean committed = false;

	public ResourceTransaction(ResourceTransactionManager manager, DigestableLocation dir) {
		this.manager = manager;
		this.dir = dir;
	}
	
	String getTransactionId() {
		return dir.getName();
	}
	
	String getUniqueTransactionItemId() {
		String id = RandomStringUtils.random(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		while (dir.resourceExists(id)) {
			id = RandomStringUtils.random(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		}
		return id;
	}
	
	DigestableResource createTransactionItem() {
		try {
			return dir.createResource(getUniqueTransactionItemId());
		} catch (DuplicateKeyError e) {
			throw new UnexpectedResourceError(
					"Unable to create a new transaction item in: " + dir.getLocation().getAbsolutePath());
		}
	}
	
	public void put(TxDigestableResource resource, byte[] data) throws EntityLockedError {
		if (resourcesMap.containsKey(resource)) {
			resourcesMap.get(resource).txResource.setData(data);
		} else {
			TransactionItem txItem = new TransactionItem();
			txItem.txResource = createTransactionItem();
			txItem.backupFile = new File(resource.getDatafile().getAbsolutePath()+".bak");
			txItem.txResource.setData(data);
			resourcesMap.put(resource, txItem);
		}
	}
	
	public byte[] getTransactionData(TxDigestableResource resource) {
		TransactionItem txItem = resourcesMap.get(resource);
		if (txItem != null) {
			if (committed && txItem.txResource.getDatafile().exists()) {
				return txItem.txResource.getData();
			} else {
				try {
					return FileUtils.readFileToByteArray(resource.getDatafile());
				} catch (IOException e) {
					throw new UnexpectedResourceError(
							"Unable to read resource datafile: " + resource.getDatafile().getAbsolutePath());
				}
			}
		}
		throw new UnexpectedResourceError(
				"Resource not in transaction: " + dir.getLocation().getAbsolutePath());
	}
	
	public byte[] getCommittedData(TxDigestableResource resource) {
		if (committed) {
			return getTransactionData(resource);
		}
		if (resourcesMap.containsKey(resource)) {
			TransactionItem txItem = resourcesMap.get(resource);
			if (txItem.backupFile.exists()) {
				try {
					return FileUtils.readFileToByteArray(txItem.backupFile);
				} catch (IOException e) {
					throw new UnexpectedResourceError(
							"Unable to read backup file: " + txItem.backupFile.getAbsolutePath());
				}
			}
			try {
				return FileUtils.readFileToByteArray(resource.getDatafile());
			} catch (IOException e) {
				throw new UnexpectedResourceError(
						"Unable to read resource datafile: " + resource.getDatafile().getAbsolutePath());
			}
		}
		throw new UnexpectedResourceError(
				"Resource not in transaction: " + dir.getLocation().getAbsolutePath());
	}
	
	void prepareCommit() throws PreCommitException {
		File backupFile = null;
		try {
			for(Map.Entry<TxDigestableResource, TransactionItem> entry : resourcesMap.entrySet()) {
				entry.getKey().lock();
				backupFile = entry.getValue().backupFile;
				FileUtils.copyFile(entry.getKey().getDatafile(), backupFile);
			}
		} catch (IOException err) {
			backupFile.delete();
			rollback();
			throw new PreCommitException(err);
		}
	}
	
	void doCommit() {
		try {
			for(Map.Entry<TxDigestableResource, TransactionItem> entry : resourcesMap.entrySet()) {
				java.nio.file.Files.move(
						entry.getValue().txResource.getDatafile().toPath(), 
						entry.getKey().getDatafile().toPath(),
						StandardCopyOption.ATOMIC_MOVE);
			}
			committed = true;
		} catch (IOException err) {
			rollback();
		}
	}
	
	void postCommit() {
		for(Map.Entry<TxDigestableResource, TransactionItem> entry : resourcesMap.entrySet()) {
			if (entry.getValue().backupFile.exists()) 
				entry.getValue().backupFile.delete();
			entry.getKey().unlock();
		}
	}

	@Override
	public void commit() throws PreCommitException {
		prepareCommit();
		doCommit();
		postCommit();
		release();
	}

	void doRollback() {
		try {
			for(Map.Entry<TxDigestableResource, TransactionItem> entry : resourcesMap.entrySet()) {
				if (entry.getValue().backupFile.exists()) 
					java.nio.file.Files.move(
							entry.getValue().backupFile.toPath(),
							entry.getKey().getDatafile().toPath(),
							StandardCopyOption.ATOMIC_MOVE);
					entry.getValue().backupFile.delete();
				entry.getKey().unlock();
			}
		} catch (IOException err) {
			throw new RollbackException(err);
		}
	}
	
	@Override
	public void rollback() {
		doRollback();
		release();
	}
	
	void release() {
		dir.clean();
		resourcesMap.keySet().stream().forEach((txResource) -> txResource.release());
		manager.releaseTransaction(this);
	}

}
