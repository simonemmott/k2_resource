package org.k2.resource.entity.key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.exception.KeyDefinitionException;
import org.k2.resource.entity.exception.UnexpectedKeyError;

import lombok.Getter;

@Getter
public class DefaultKeyGetter<K,E> implements KeyGetter<K,E> {
	
	private final Class<K> keyType;
	private final Class<E> entityType;
	private KeyGetter<K,E> keyGetter = null;
	
	public DefaultKeyGetter (Class<K> keyType, Class<E> entityType) throws KeyDefinitionException {
		this.keyType = keyType;
		this.entityType = entityType;
		
		for (Field field : entityType.getDeclaredFields()) {
			for (Annotation ann : field.getAnnotations()) {
				if (ann.annotationType().equals(Key.class)) {
					if (field.getType().isAssignableFrom(keyType)) {
						field.setAccessible(true);
						keyGetter = (E entity) -> {
							try {
								return (K)field.get(entity);
							} catch (IllegalArgumentException | IllegalAccessException err) {
								throw new UnexpectedKeyError(err);
							}
						};
						break;
					}
					throw new KeyDefinitionException(
							entityType, 
							"The field annotated with @Key is not of type: "+keyType.getName());
				}
				if (keyGetter != null) break; 
			}
			if (keyGetter != null) break;
		}
		for (Method method : entityType.getDeclaredMethods()) {
			for (Annotation ann : method.getAnnotations()) {
				if (ann.annotationType().equals(Key.class)) {
					if (method.getReturnType().isAssignableFrom(keyType)) {
						method.setAccessible(true);
						keyGetter = (E entity) -> {
							try {
								return (K)method.invoke(entity);
							} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException err) {
								throw new UnexpectedKeyError(err);
							}
						};
						break;
					}
					throw new KeyDefinitionException(
							entityType, 
							"The method annotated with @Key does not return type: "+keyType.getName());
				}
				if (keyGetter != null) break; 
			}
			if (keyGetter != null) break;
		}
		if (keyGetter == null)
			throw new KeyDefinitionException(entityType, "Unable to identify key field or method");
	}

	@Override
	public K get(E entity) {
		return keyGetter.get(entity);
	}

}
