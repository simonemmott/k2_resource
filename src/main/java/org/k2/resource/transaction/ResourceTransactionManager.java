package org.k2.resource.transaction;

import org.apache.commons.lang3.RandomStringUtils;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.location.DigestableLocation;

public class ResourceTransactionManager implements TransactionManager {
	
	private final DigestableLocation dir;
	private final ThreadLocal<ResourceTransaction> threadLocalTransaction;

	public ResourceTransactionManager(DigestableLocation dir) {
		this.dir = dir;
		this.threadLocalTransaction = ThreadLocal.withInitial(() -> {
			return createTransaction();
		});
	}
	
	String getUniqueTransactionId() {
		String id = RandomStringUtils.random(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		while (dir.locationExists(id)) {
			id = RandomStringUtils.random(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		}
		return id;
	}
	
	ResourceTransaction createTransaction() {
		try {
			return new ResourceTransaction(
					this, 
					dir.createLocation(getUniqueTransactionId()));
		} catch (DuplicateKeyError e) {
			throw new UnexpectedResourceError(
					"Unable to create a new transaction in: " + dir.getLocation().getAbsolutePath());
		}
	}
	
	void releaseTransaction(ResourceTransaction transaction) {
		try {
			threadLocalTransaction.remove();
			dir.removeLocation(transaction.getTransactionId());
		} catch (MissingKeyError e) {
			throw new UnexpectedResourceError(
					"Unable to release transaction in: " + dir.getLocation().getAbsolutePath());
		}
	}

	@Override
	public Transaction getTransaction() {
		return threadLocalTransaction.get();
	}

}
