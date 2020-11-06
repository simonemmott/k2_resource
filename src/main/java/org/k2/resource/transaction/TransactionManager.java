package org.k2.resource.transaction;

import org.k2.resource.location.DigestableLocation;

public interface TransactionManager {
	static TransactionManager create(DigestableLocation transactionsDir) {
		return new ResourceTransactionManager(transactionsDir);
	}
	
	Transaction getTransaction();
	boolean hasTransaction();
	
	
}
