package org.k2.resource.entity.key;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.k2.resource.entity.annotation.Key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class DefaultKeySetterTest {
	
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
	public void testDefaultKeySetterGetsAnontatedField() throws Exception {
		DefaultKeySetter<String, TypeWithKeyField> keySetter = new DefaultKeySetter(String.class, TypeWithKeyField.class);
		TypeWithKeyField entity = new TypeWithKeyField("KEY", "NOT_KEY");
		assertThat(keySetter.getKeySetter()).isNotNull();
		keySetter.set(entity, "UPDATED_KEY");
		assertThat(entity.aKey).isEqualTo("UPDATED_KEY");
	}

	@Test
	public void testDefaultKeySetterGetsAnontatedMember() throws Exception {
		DefaultKeySetter<String, TypeWithKeyMember> keySetter = new DefaultKeySetter(String.class, TypeWithKeyMember.class);
		TypeWithKeyMember entity = new TypeWithKeyMember("KEY", "NOT_KEY");
		assertThat(keySetter.getKeySetter()).isNotNull();
		keySetter.set(entity, "UPDATED_KEY");
		assertThat(entity.aKey).isEqualTo("UPDATED_KEY");
	}

	@Test
	public void testDefaultKeySetterGetsAnontatedFieldWithoutKeyType() throws Exception {
		DefaultKeySetter<String, TypeWithKeyField> keySetter = new DefaultKeySetter(TypeWithKeyField.class);
		TypeWithKeyField entity = new TypeWithKeyField("KEY", "NOT_KEY");
		assertThat(keySetter.getKeySetter()).isNotNull();
		keySetter.set(entity, "UPDATED_KEY");
		assertThat(entity.aKey).isEqualTo("UPDATED_KEY");
	}

	@Test
	public void testDefaultKeySetterGetsAnontatedMemberWithoutKeyType() throws Exception {
		DefaultKeySetter<String, TypeWithKeyMember> keySetter = new DefaultKeySetter(TypeWithKeyMember.class);
		TypeWithKeyMember entity = new TypeWithKeyMember("KEY", "NOT_KEY");
		assertThat(keySetter.getKeySetter()).isNotNull();
		keySetter.set(entity, "UPDATED_KEY");
		assertThat(entity.aKey).isEqualTo("UPDATED_KEY");
	}

}
