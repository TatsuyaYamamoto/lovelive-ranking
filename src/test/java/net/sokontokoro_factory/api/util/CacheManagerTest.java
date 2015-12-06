package net.sokontokoro_factory.api.util;

import static org.junit.Assert.*;

import javax.ws.rs.core.CacheControl;

import org.junit.Test;

public class CacheManagerTest {

	@Test
	public void test() {
		CacheControl actual = CacheManager.getNoCacheAndStoreControl();
		assertTrue(actual.isNoCache());
		assertTrue(actual.isNoStore());
		assertTrue(actual.isMustRevalidate());
		System.out.println(actual.toString());

	}

}
