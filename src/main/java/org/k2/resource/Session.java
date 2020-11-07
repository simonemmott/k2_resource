package org.k2.resource;

import java.util.function.Supplier;

import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

public interface Session {
	void save() throws MissingKeyError, MutatingEntityError, DuplicateKeyError, EntityLockedError;
	<R> R doInTransaction(Supplier<R> expression) throws Throwable;

}
