package org.k2.resource.binary;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.k2.resource.binary.BinaryEntity;

class BinaryEntityTest {
	

	@Test
	void newInstanceWithKeyTest() {
		String key = "KEY";
		String data = "DATA";
		BinaryEntity be = new BinaryEntity("KEY", "DATA".getBytes());
		
		assertThat(be.getKey()).isEqualTo("KEY");
		assertThat(be.getData()).isEqualTo("DATA".getBytes());
		assertThat(be.getChecksum()).isEqualTo(-1);
	}

}
