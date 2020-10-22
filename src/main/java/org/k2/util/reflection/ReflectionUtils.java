package org.k2.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.k2.util.reflection.exception.MissingAnnotationError;
import org.k2.util.reflection.exception.ReflectionError;

public class ReflectionUtils {

	public static Field getAnnotatedField(
			Class<?> cls, 
			Class<? extends Annotation> annType) throws MissingAnnotationError {
		for (Field field : cls.getDeclaredFields()) {
			for (Annotation ann : field.getAnnotations()) {
				if (ann.annotationType().equals(annType)) {
					return field;
				}
			}
		}
		throw new MissingAnnotationError(cls, annType, "on fields");
	}

	public static Field getAnnotatedField(
			Class<?> cls, 
			Class<? extends Annotation> annType, 
			Class<?> fieldType) throws ReflectionError, MissingAnnotationError {
		for (Field field : cls.getDeclaredFields()) {
			for (Annotation ann : field.getAnnotations()) {
				if (ann.annotationType().equals(annType)) {
					if (field.getType().isAssignableFrom(fieldType)) {
						return field;
					}
					throw new ReflectionError(
							cls, 
							"The field annotated with @Key is not of type: "+fieldType.getName());
				}
			}
		}
		throw new MissingAnnotationError(cls, annType, "on fields");
	}

	public static Method getAnnotatedMethod(
			Class<?> cls, 
			Class<? extends Annotation> annType, 
			Class<?> ... argTypes) throws MissingAnnotationError {
		for (Method method : cls.getDeclaredMethods()) {
			if (method.getParameterCount() == argTypes.length) {
				if (Arrays.asList(argTypes).equals(Arrays.asList(method.getParameterTypes()))) {
					for (Annotation ann : method.getAnnotations()) {
						if (ann.annotationType().equals(annType)) {
							return method;
						}
					}
				}
			}
		}
		throw new MissingAnnotationError(cls, annType, "on methods with parameters: "+Arrays.asList(argTypes).toString());
	}

	public static Method getAnnotatedMethodReturnsType(
			Class<?> cls, 
			Class<? extends Annotation> annType, 
			Class<?> returnType, 
			Class<?> ... argTypes) throws ReflectionError, MissingAnnotationError {
		for (Method method : cls.getDeclaredMethods()) {
			if (method.getParameterCount() == argTypes.length) {
				if (Arrays.asList(argTypes).equals(Arrays.asList(method.getParameterTypes()))) {
					for (Annotation ann : method.getAnnotations()) {
						if (ann.annotationType().equals(annType)) {
							System.out.println(method.getReturnType());
							if (method.getReturnType().isAssignableFrom(returnType) ||
									(method.getReturnType() == void.class &&
									(returnType.equals(Void.class) || returnType.equals(void.class)))) {
								return method;
							}
							throw new ReflectionError(
									cls, 
									"The method annotated with @Key does not return type: "+returnType.getName());
						}
					}
				}
			}
		}
		throw new MissingAnnotationError(cls, annType, "on methods returning: "+returnType.getName()+"with parameters: "+Arrays.asList(argTypes).toString());
	}

}
