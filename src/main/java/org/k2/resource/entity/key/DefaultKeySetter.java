package org.k2.resource.entity.key;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.exception.KeyDefinitionException;
import org.k2.resource.entity.exception.UnexpectedKeyError;
import org.k2.util.reflection.ReflectionUtils;
import org.k2.util.reflection.exception.MissingAnnotationError;
import org.k2.util.reflection.exception.ReflectionError;

import lombok.Getter;

@Getter
public class DefaultKeySetter<K,E> implements KeySetter<K,E> {
	
	private Class<K> keyType;
	private final Class<E> entityType;
	private KeySetter<K,E> keySetter = null;
	
	public DefaultKeySetter (Class<K> keyType, Class<E> entityType) throws EntityConfigurationException {
		this.keyType = keyType;
		this.entityType = entityType;
		
		try {
			Field keyField = ReflectionUtils.getAnnotatedField(entityType, Key.class, keyType);
			keyField.setAccessible(true);
			keySetter = (E entity, K key) -> {
				try {
					keyField.set(entity, key);
				} catch (IllegalArgumentException | IllegalAccessException err) {
					throw new UnexpectedKeyError(err);
				}
			};
			return;
		} catch (MissingAnnotationError err) {
			try {
				Method keyMethod = ReflectionUtils.getAnnotatedMethodReturnsType(entityType, Key.class, Void.class, keyType);
				keyMethod.setAccessible(true);
				keySetter = (E entity, K key) -> {
					try {
						keyMethod.invoke(entity, key);
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
						"The method annotated with @Key does not return void", e);
			}
		} catch (ReflectionError e) {
			throw new KeyDefinitionException(
					entityType, 
					"The field annotated with @Key is not of type: "+keyType.getName());
		}
	}

	public DefaultKeySetter (Class<E> entityType) throws EntityConfigurationException {
		this.entityType = entityType;
		
		try {
			Field keyField = ReflectionUtils.getAnnotatedField(entityType, Key.class);
			keyField.setAccessible(true);
			keySetter = (E entity, K key) -> {
				try {
					keyField.set(entity, key);
				} catch (IllegalArgumentException | IllegalAccessException err) {
					throw new UnexpectedKeyError(err);
				}
			};
			this.keyType = (Class<K>) keyField.getType();
			return;
		} catch (MissingAnnotationError err) {
			try {
				Method keyMethod = ReflectionUtils.getAnnotatedMethodReturnsType(entityType, Key.class, Void.class, 1);
				keyMethod.setAccessible(true);
				keySetter = (E entity, K key) -> {
					try {
						keyMethod.invoke(entity, key);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						throw new UnexpectedKeyError(e);
					}
				};
				this.keyType = (Class<K>) keyMethod.getParameterTypes()[0];
				return;
			} catch (MissingAnnotationError e) {
				throw new KeyDefinitionException(entityType, "Unable to identify key field or method");
			} catch (ReflectionError e) {
				throw new KeyDefinitionException(
						entityType, 
						"The method annotated with @Key does not return void", e);
			}
		}
	}

	@Override
	public void set(E entity, K key) {
		keySetter.set(entity, key);
	}

}
