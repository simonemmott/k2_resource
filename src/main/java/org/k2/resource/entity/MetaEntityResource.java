package org.k2.resource.entity;

import org.k2.core.serialize.ClassDeserializer;
import org.k2.core.serialize.ClassSerializer;
import org.k2.resource.MetaResource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class MetaEntityResource extends MetaResource {

	@JsonSerialize(using = ClassSerializer.class)
	@JsonDeserialize(using = ClassDeserializer.class)
	Class<?> keyType;
	@JsonSerialize(using = ClassSerializer.class)
	@JsonDeserialize(using = ClassDeserializer.class)
	Class<?> entityType;
	String entityName;
	boolean prettyPrint = true;

}
