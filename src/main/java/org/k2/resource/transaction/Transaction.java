package org.k2.resource.transaction;

import org.k2.resource.transaction.exception.PreCommitException;

public interface Transaction {

	void commit() throws PreCommitException;
	void rollback();
}
