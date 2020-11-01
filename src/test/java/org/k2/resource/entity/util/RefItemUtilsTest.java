package org.k2.resource.entity.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.annotation.RefItem;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.exception.ResourceConfigurationException;

class RefItemUtilsTest {
	
	@Getter
	@Setter
	@AllArgsConstructor
	@RefItem
	class TestRefItem {		
		@Key
		private String key;
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	@RefItem("TYPE_NAME")
	class TestRefItemWithTypeName {		
		@Key
		private String key;
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	class TestItem {		
		@Key
		private String key;
	}
	
	@Test
	void testIsRefItemShouldReturnTrueIfEntityClassIsAnnotatedWithRefItem() throws Exception {
		assertThat(RefItemUtils.isRefItem(TestRefItem.class)).isTrue();
	}
	
	@Test
	void testIsRefItemShouldReturnTrueIfEntityClassIsNotAnnotatedWithRefItem() throws Exception {
		assertThat(RefItemUtils.isRefItem(TestItem.class)).isFalse();
	}
	
	@Test
	void testGetReferenceTypeThrowsEntityConfigurationExceptionIfNotRefItem() {
		
		assertThatThrownBy(() -> RefItemUtils.getTypeReference(TestItem.class))
				.isInstanceOf(EntityConfigurationException.class);
	}
	
	@Test
	void testGetReferenceTypeShouldReturnTheTypeNameByDefault() throws Exception {
		assertThat(RefItemUtils.getTypeReference(TestRefItem.class)).isEqualTo("TestRefItem");
	}
	
	@Test
	void testGetReferenceTypeShouldReturnTheRefItemValueIfItIsGiven() throws Exception {
		assertThat(RefItemUtils.getTypeReference(TestRefItemWithTypeName.class)).isEqualTo("TYPE_NAME");
	}
	
}








