package org.k2.resource.entity.util;

import org.k2.resource.entity.annotation.RefItem;
import org.k2.resource.entity.exception.EntityConfigurationException;

public class RefItemUtils {

	public static boolean isRefItem(Class<?> refType) {
		return refType.getAnnotation(RefItem.class) != null;
	}
	
	public static void checkIsRefItem(Class<?> refType) throws EntityConfigurationException {
		if (!isRefItem(refType)) throw new EntityConfigurationException(refType, "is not a RefItem");
	}

	public static String getTypeReference(Class<?> refType) throws EntityConfigurationException {
		checkIsRefItem(refType);
		RefItem refItem = refType.getAnnotation(RefItem.class);
		if (refItem.value().equals(""))
			return refType.getSimpleName();
		else
			return refItem.value();
	}


}
