package org.k2.resource;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

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

	@Test
	void newInstanceWithoutKeyTest() {
		String data = "DATA";
		BinaryEntity be = bef.create(data);
		
		assertThat(be.getKey()).isNull();
		assertThat(be.getData()).isEqualTo("DATA".getBytes());
		assertThat(be.getChecksum()).isEqualTo(2607161047L);
		
		be.setKey("KEY");
		assertThat(be.getKey()).isEqualTo("KEY");
	}

}
