package org.k2.resource.entity.core;

import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.annotation.RefItem;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RefItem()
@EqualsAndHashCode()
public class TestEntity2 {		
	public TestEntity2() {}
	@Key
	private String key;
}
