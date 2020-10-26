package org.k2.resource.entity.simple;

import org.k2.resource.entity.ResourceSession;

public class SimpleResourceSession implements ResourceSession {

	public SimpleResourceSession() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public <K> K get(Class<K> entityType, Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean has(Class<?> entityType, Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <E> void put(Class<E> entityType, Object key, E obj, long checksum) {
		// TODO Auto-generated method stub
		
	}

}
