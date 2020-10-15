package org.k2.resource.binary;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.binary.BinaryEntityFactory;

class BinaryEntityTest {
	
	private BinaryEntityFactory bef = new BinaryEntityFactory();

	@Test
	void newInstanceWithKeyTest() {
		String key = "KEY";
		String data = "DATA";
		BinaryEntity be = bef.create(key, data);
		
		assertThat(be.getKey()).isEqualTo("KEY");
		assertThat(be.getData()).isEqualTo("DATA".getBytes());
		assertThat(be.getChecksum()).isEqualTo(2607161047L);
	}

}