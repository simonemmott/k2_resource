package org.k2.resource.entity;

import org.k2.resource.MetaResource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaEntityResource extends MetaResource {

	Class<?> keyType;
	Class<?> entityType;

}
