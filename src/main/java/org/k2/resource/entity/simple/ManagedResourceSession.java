package org.k2.resource.entity.simple;

import java.util.Set;
import java.util.function.Supplier;

import org.k2.resource.Resource;
import org.k2.resource.entity.EntityResourceManager;
import org.k2.resource.entity.ManagedEntityResource;
import org.k2.resource.entity.ResourceSession;
import org.k2.resource.entity.core.EntityCache;
import org.k2.resource.entity.core.SimpleEntityCache;
import org.k2.resource.entity.exception.ManagedResourceError;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;

public class ManagedResourceSession extends SimpleResourceSession implements ResourceSession {
	
	private final EntityResourceManager resourceManager;
	
	public ManagedResourceSession(EntityResourceManager resourceManager) {
		super();
		this.resourceManager = resourceManager;
	}
	
	@Override
	public void save() throws MissingKeyError, 
			MutatingEntityError, 
			DuplicateKeyError,
			EntityLockedError {
		cache.forEach((Class<?> keyType, 
				Object key, 
				Class<?> entityType, 
				Object entity, 
				boolean isNew, 
				boolean isChanged, 
				boolean isDeleted) -> {
					ManagedEntityResource<?, ?> resource;
					try {
						resource = resourceManager.getResource(entityType);
						if (isNew) {
							resource.createFromSession(key, entity);
						} else if (isDeleted) {
							resource.removeFromSession(key);
						} else if (isChanged) {
							resource.updateFromSession(key, entity);
						}
					} catch (ManagedResourceError e) {
						throw new UnexpectedResourceError(
								"Unable to get resource for entity type: " + entityType.getName()
								, e);
					}
				});
		
	}

	@Override
	public <R> R doInTransaction(Supplier<R> expression) throws Throwable {
		return expression.get();
	}

}
