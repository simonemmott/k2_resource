package org.k2.util.reflection.exception;

import java.lang.annotation.Annotation;

import lombok.Getter;

public class MissingAnnotationError extends ReflectionError {

	@Getter
	private final Class<? extends Annotation> annType;
	public MissingAnnotationError(Class<?> type, Class<? extends Annotation> annType) {
		super(type, "No annotation of type: "+annType.getName());
		this.annType = annType;
	}

	public MissingAnnotationError(Class<?> type, Class<? extends Annotation> annType, String message) {
		super(type, "No annotation of type: "+annType.getName()+" - "+message);
		this.annType = annType;
	}

	public MissingAnnotationError(Class<?> type, Class<? extends Annotation> annType, Throwable cause) {
		super(type, "No annotation of type: "+annType.getName(), cause);
		this.annType = annType;
	}

	public MissingAnnotationError(Class<?> type, Class<? extends Annotation> annType, String message, Throwable cause) {
		super(type, "No annotation of type: "+annType.getName()+" - "+message, cause);
		this.annType = annType;
	}

	public MissingAnnotationError(Class<?> type, Class<? extends Annotation> annType, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(type, "No annotation of type: "+annType.getName()+" - "+message, cause, enableSuppression, writableStackTrace);
		this.annType = annType;
	}

}
