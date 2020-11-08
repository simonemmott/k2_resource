package org.k2.resource;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class MetaResource {

	private String datafileExtension;
	
	public MetaResource setDatafileExtension(String datafileExtension) {
		this.datafileExtension = datafileExtension;
		return this;
	}

}
