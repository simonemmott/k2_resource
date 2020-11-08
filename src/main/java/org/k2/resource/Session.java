package org.k2.resource;

import java.util.function.Supplier;

import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.transaction.Transaction;

public interface Session {
	public interface ThrowableSupplier<R> {
		R get() throws Throwable;
	}
	public interface ThrowableRunable {
		void run() throws Throwable;
	}
	void save() throws MissingKeyError, MutatingEntityError, DuplicateKeyError, EntityLockedError;
	<R> R doInTransaction(ThrowableSupplier<R> expression) throws Throwable;
	void doInTransaction(ThrowableRunable runnable) throws Throwable;
	Transaction getTransaction();
	void clear();

}
