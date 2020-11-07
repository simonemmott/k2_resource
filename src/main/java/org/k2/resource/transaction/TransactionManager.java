package org.k2.resource.transaction;

import org.k2.resource.location.DigestableLocation;

public interface TransactionManager {

	public static final String TRANSACTIONS_DIR_NAME = "__transactions__";

	static TransactionManager create(DigestableLocation transactionsDir) {
		return new ResourceTransactionManager(transactionsDir);
	}
	
	Transaction getTransaction();
	boolean hasTransaction();
	
	
}
