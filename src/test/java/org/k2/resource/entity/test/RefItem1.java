package org.k2.resource.entity.test;

import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.annotation.RefItem;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@RefItem
public class RefItem1 {
	
	@Key
	private String key;
	private String name;
	private Integer data;

}
