package org.k2.resource.binary;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.k2.resource.binary.BinaryEntityImpl;

class BinaryEntityTest {
	

	@Test
	void newInstanceWithKeyTest() {
		String key = "KEY";
		String data = "DATA";
		BinaryEntityImpl be = new BinaryEntityImpl("KEY", "DATA".getBytes());
		
		assertThat(be.getKey()).isEqualTo("KEY");
		assertThat(be.getData()).isEqualTo("DATA".getBytes());
		assertThat(be.getChecksum()).isEqualTo(-1);
	}

}
