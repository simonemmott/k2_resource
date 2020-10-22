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
import org.k2.util.reflection.ReflectionUtils;
import org.k2.util.reflection.exception.MissingAnnotationError;
import org.k2.util.reflection.exception.ReflectionError;

import lombok.Getter;

@Getter
public class DefaultKeyGetter<K,E> implements KeyGetter<K,E> {
	
	private final Class<K> keyType;
	private final Class<E> entityType;
	private KeyGetter<K,E> keyGetter = null;
	
	public DefaultKeyGetter (Class<K> keyType, Class<E> entityType) throws KeyDefinitionException {
		this.keyType = keyType;
		this.entityType = entityType;
		
		try {
			Field keyField = ReflectionUtils.getAnnotatedField(entityType, Key.class, keyType);
			keyField.setAccessible(true);
			keyGetter = (E entity) -> {
				try {
					return (K)keyField.get(entity);
				} catch (IllegalArgumentException | IllegalAccessException err) {
					throw new UnexpectedKeyError(err);
				}
			};
			return;
		} catch (MissingAnnotationError err) {
			try {
				Method keyMethod = ReflectionUtils.getAnnotatedMethodReturnsType(entityType, Key.class, keyType);
				keyMethod.setAccessible(true);
				keyGetter = (E entity) -> {
					try {
						return (K)keyMethod.invoke(entity);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						throw new UnexpectedKeyError(e);
					}
				};
				return;
			} catch (MissingAnnotationError e) {
				throw new KeyDefinitionException(entityType, "Unable to identify key field or method");
			} catch (ReflectionError e) {
				throw new KeyDefinitionException(
						entityType, 
						"The method annotated with @Key does not return type: "+keyType.getName());
			}
		} catch (ReflectionError e) {
			throw new KeyDefinitionException(
					entityType, 
					"The field annotated with @Key is not of type: "+keyType.getName());
		}
	}

	@Override
	public K get(E entity) {
		return keyGetter.get(entity);
	}

}
