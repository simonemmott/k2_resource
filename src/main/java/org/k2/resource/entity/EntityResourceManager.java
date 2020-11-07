package org.k2.resource.entity;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.k2.resource.Resource;
import org.k2.resource.ResourceManager;
import org.k2.resource.Session;
import org.k2.resource.entity.exception.ManagedResourceError;
import org.k2.resource.entity.exception.ManagedResourceInitializationError;
import org.k2.resource.entity.simple.ManagedResourceSession;
import org.k2.resource.entity.simple.SimpleResourceSession;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.location.DigestableLocation;
import org.k2.resource.location.SimpleDigestableLocation;
import org.k2.resource.location.TxDigestableLocation;
import org.k2.resource.location.TxDigestableResource;
import org.k2.resource.transaction.ResourceTransactionManager;
import org.k2.resource.transaction.TransactionManager;

import lombok.Getter;

public class EntityResourceManager implements ResourceManager {
	
	public static EntityResourceManager create(File resourceDir) throws ManagedResourceInitializationError {
		if (!resourceDir.exists()) {
			try {
				FileUtils.forceMkdir(resourceDir);
			} catch (IOException e) {
				throw new ManagedResourceInitializationError(
						"unable to create the resource dir: " + resourceDir.getAbsolutePath(), e);
			}
		}
		if (!resourceDir.isDirectory())
			throw new ManagedResourceInitializationError(
					"unable to create the resource dir: " + resourceDir.getAbsolutePath() + " - already exists but is not a directory");
		File transactionsDir = FileUtils.getFile(resourceDir, TransactionManager.TRANSACTIONS_DIR_NAME);
		if (!transactionsDir.exists()) {
			try {
				FileUtils.forceMkdir(transactionsDir);
			} catch (IOException e) {
				throw new ManagedResourceInitializationError(
						"unable to create the transactions dir: " + transactionsDir.getAbsolutePath(), e);
			}
		}
		if (!transactionsDir.isDirectory())
			throw new ManagedResourceInitializationError(
					"unable to create the transactions dir: " + transactionsDir.getAbsolutePath() + " - already exists but is not a directory");
		
		return open(resourceDir);
	}
	
	public static EntityResourceManager open(File resourceDir) throws ManagedResourceInitializationError {
		if (!resourceDir.exists()) 
			throw new ManagedResourceInitializationError(
					"the resource directory: " + resourceDir.getAbsolutePath() + " does not exist");
		if (!resourceDir.isDirectory()) 
			throw new ManagedResourceInitializationError(
					"the resource directory: " + resourceDir.getAbsolutePath() + " is not a directory");
		if (!resourceDir.canRead()) 
			throw new ManagedResourceInitializationError(
					"the resource directory: " + resourceDir.getAbsolutePath() + " is not readable");
		if (!resourceDir.canWrite()) 
			throw new ManagedResourceInitializationError(
					"the resource directory: " + resourceDir.getAbsolutePath() + " is not writable");
		File transactionsDir = FileUtils.getFile(resourceDir, TransactionManager.TRANSACTIONS_DIR_NAME);
		if (!transactionsDir.exists()) 
			throw new ManagedResourceInitializationError(
					"the transaction directory: " + transactionsDir.getAbsolutePath() + " does not exist");
		if (!transactionsDir.isDirectory()) 
			throw new ManagedResourceInitializationError(
					"the transaction directory: " + transactionsDir.getAbsolutePath() + " is not a directory");
		if (!transactionsDir.canRead()) 
			throw new ManagedResourceInitializationError(
					"the transaction directory: " + transactionsDir.getAbsolutePath() + " is not readable");
		if (!transactionsDir.canWrite()) 
			throw new ManagedResourceInitializationError(
					"the transaction directory: " + transactionsDir.getAbsolutePath() + " is not writable");
		DigestableLocation.Digestor digestor = DigestableLocation.defaultDigestor();
		ResourceTransactionManager txManager;
		try {
			txManager = new ResourceTransactionManager(new SimpleDigestableLocation(transactionsDir, digestor));
			EntityResourceManager rm = new EntityResourceManager(new TxDigestableLocation(resourceDir, digestor), txManager);
			return rm;
		} catch (ResourceConfigurationException e) {
			throw new ManagedResourceInitializationError("Resource configuration error opening resource: " + resourceDir.getAbsolutePath());
		}
	}
	
	private ResourceSession createSession() {
		return new ManagedResourceSession(this);
	}
	
	@Getter
	private final TxDigestableLocation location;
	private final ThreadLocal<ResourceSession> threadLocalSession = ThreadLocal.withInitial(() -> createSession());
	@Getter
	private final ResourceTransactionManager transactionManager;
	final Map<Class<?>,ManagedEntityResource<?,?>> resources = new HashMap<>();
	

	private EntityResourceManager(TxDigestableLocation location, ResourceTransactionManager transactionManager) {
		this.location = location;
		this.location.setResourceManager(this);
		this.transactionManager = transactionManager;
	}

	@Override
	public ResourceSession getSession() {
		return threadLocalSession.get();
	}

	@Override
	public <K, E> ManagedEntityResource<K, E> getResource(Class<K> keyType, Class<E> entityType) throws ManagedResourceError {
		ManagedEntityResource<?,?> resource = resources.get(entityType);
		if (resource == null) {
			throw new ManagedResourceError("Not managing resources for type: " + entityType.getName());
		}
		try {
			return (ManagedEntityResource<K, E>) resource;
		} catch (ClassCastException err) {
			throw new ManagedResourceError(
					MessageFormat.format(
							"Managed resource for type: {0} not applicaple to keyType: {1}",
							keyType.getName(),
							entityType.getName()));
		}
	}

	@Override
	public <E> ManagedEntityResource<?, E> getResource(Class<E> entityType) throws ManagedResourceError {
		Resource<?,?> resource = resources.get(entityType);
		if (resource == null) {
			throw new ManagedResourceError("Not managing resources for type: " + entityType.getName());
		}
		try {
			return (ManagedEntityResource<?, E>) resource;
		} catch (ClassCastException err) {
			throw new ManagedResourceError(
					MessageFormat.format(
							"Managed resource for type: {0} not applicaple to Type: {0}",
							entityType.getName()));
		}
	}

}
