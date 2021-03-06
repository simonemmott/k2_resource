package org.k2.resource.entity.simple;

import org.k2.resource.entity.annotation.Key;

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
public class SimpleEntity {
	
	@Key
	private String key;
	private String name;
	private Integer data;

}
