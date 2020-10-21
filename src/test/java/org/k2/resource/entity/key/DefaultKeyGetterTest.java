package org.k2.resource.entity.key;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.k2.resource.entity.annotation.Key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class DefaultKeyGetterTest {
	
	@AllArgsConstructor
	public class TypeWithKeyMember {
		@Getter(onMethod_= @__({@Key}))
		@Setter(onMethod_= @__({@Key}))
		private String aKey;
		@Getter
		private String notKey;
	}

	@AllArgsConstructor
	public class TypeWithKeyField {
		@Key
		private String aKey;
		private String notKey;
	}

	@Test
	public void testDefaultKeYGetterGetsAnontatedField() throws Exception {
		DefaultKeyGetter<String, TypeWithKeyField> keyGetter = new DefaultKeyGetter(String.class, TypeWithKeyField.class);
		TypeWithKeyField entity = new TypeWithKeyField("KEY", "NOT_KEY");
		assertThat(keyGetter.getKeyGetter()).isNotNull();
		assertThat(keyGetter.get(entity)).isEqualTo("KEY");
	}

	@Test
	public void testDefaultKeYGetterGetsAnontatedMember() throws Exception {
		DefaultKeyGetter<String, TypeWithKeyMember> keyGetter = new DefaultKeyGetter(String.class, TypeWithKeyMember.class);
		TypeWithKeyMember entity = new TypeWithKeyMember("KEY", "NOT_KEY");
		assertThat(keyGetter.getKeyGetter()).isNotNull();
		assertThat(keyGetter.get(entity)).isEqualTo("KEY");
	}

}
