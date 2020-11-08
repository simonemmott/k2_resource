package org.k2.util.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.k2.util.object.exception.UnexpectedObjectError;

public class ObjectUtils {
	
	private static Map<Class<?>, Set<Field>> typeFields = new HashMap<>();
	private static Map<Class<?>, Constructor<?>> typeConstructors = new HashMap<>();
	
	public static <O> Constructor<O> noArgConstructor(O obj) {
		return noArgConstructor((Class<O>) obj.getClass());
	}
	public static <O> Constructor<O> noArgConstructor(Class<O> type) {
		Constructor<O> c = (Constructor<O>) typeConstructors.get(type);
		if (c == null) {
			try {
				c = type.getConstructor();
				typeConstructors.put(type, c);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new UnexpectedObjectError("No publlically available no-arg constructor available", e);
			}
		}
		return c;
	}
	public static Set<Field> fieldSetters(Object obj) {
		return fieldSetters(obj.getClass());
	}
	public static Set<Field> fieldSetters(Class<?> type) {
		Set<Field> fields = typeFields.get(type);
		if (fields == null) {
			final Set<Field> fs = new HashSet();
			Arrays.asList(type.getFields()).stream().forEach((Field field) -> {
				if (! field.isSynthetic()) {
					field.setAccessible(true);
					fs.add(field);
				}
			});
			typeFields.put(type, fs);
			return fs;
		}
		return fields;
	}

	public static <O> O deepclone(O obj) {
		if (obj.getClass().isArray()) {
			List<Object> clonedArray = new ArrayList<>();
			Arrays.asList(obj).stream().forEach((Object item) -> {
				clonedArray.add(deepclone(item));
			});
			return (O) clonedArray.toArray();
		} else if (
				obj instanceof Byte ||
				obj instanceof Short ||
				obj instanceof Integer ||
				obj instanceof Long ||
				obj instanceof Float ||
				obj instanceof Double ||
				obj instanceof Date ||
				obj instanceof String ||
				obj instanceof Boolean
				) {
			return obj;
		} else if (obj instanceof HashMap) {
			Map cloneMap = new HashMap<>();
			((Map<?,?>) obj).entrySet().stream().forEach((Entry<?,?> entry) -> {
				cloneMap.put((Object)deepclone(entry.getKey()), (Object)deepclone(entry.getValue()));
			});
			return (O) cloneMap;
		} else if (obj instanceof TreeMap) {
			Map cloneMap = new TreeMap<>();
			((Map<?,?>) obj).entrySet().stream().forEach((Entry<?,?> entry) -> {
				cloneMap.put((Object)deepclone(entry.getKey()), (Object)deepclone(entry.getValue()));
			});
			return (O) cloneMap;
		} else if (obj instanceof Map) {
			Map cloneMap = new HashMap<>();
			((Map<?,?>) obj).entrySet().stream().forEach((Entry<?,?> entry) -> {
				cloneMap.put((Object)deepclone(entry.getKey()), (Object)deepclone(entry.getValue()));
			});
			return (O) cloneMap;
		} else if (obj instanceof ArrayList) {
			List cloneList = new ArrayList<>();
			((List) obj).stream().forEach((Object item) -> {
				cloneList.add(deepclone(item));
			});
			return (O) cloneList;
		} else if (obj instanceof LinkedList) {
			List cloneList = new LinkedList<>();
			((List) obj).stream().forEach((Object item) -> {
				cloneList.add(deepclone(item));
			});
			return (O) cloneList;
		} else if (obj instanceof List) {
			List cloneList = new ArrayList<>();
			((List) obj).stream().forEach((Object item) -> {
				cloneList.add(deepclone(item));
			});
			return (O) cloneList;
		} else if (obj instanceof HashSet) {
			Set cloneSet = new HashSet<>();
			((Set) obj).stream().forEach((Object item) -> {
				cloneSet.add(deepclone(item));
			});
			return (O) cloneSet;
		} else if (obj instanceof TreeSet) {
			Set cloneSet = new TreeSet<>();
			((Set) obj).stream().forEach((Object item) -> {
				cloneSet.add(deepclone(item));
			});
			return (O) cloneSet;
		} else if (obj instanceof Set) {
			Set cloneSet = new HashSet<>();
			((Set) obj).stream().forEach((Object item) -> {
				cloneSet.add(deepclone(item));
			});
			return (O) cloneSet;
		}

		O clone;
		try {
			clone = noArgConstructor(obj).newInstance();			
		} catch (Throwable e) {
			throw new UnexpectedObjectError(
					"No publlically available no-arg constructor available for class: " + obj.getClass().getName()
					, e);
		}
		for (Field field : fieldSetters(obj)) {
			try {
				Object value = field.get(obj);
				if (value != null) {
					field.set(clone, deepclone(field.get(obj)));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new UnexpectedObjectError("No publlically available no-arg constructor available", e);
			}
		}
		return clone;
		
	}

}
