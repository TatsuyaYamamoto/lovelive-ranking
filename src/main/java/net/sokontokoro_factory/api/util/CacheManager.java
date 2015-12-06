package net.sokontokoro_factory.api.util;

import javax.ws.rs.core.CacheControl;

public class CacheManager {
	public static CacheControl getNoCacheAndStoreControl(){
		final CacheControl cacheControl = new CacheControl();
	    cacheControl.setNoCache(true);
	    cacheControl.setNoStore(true);
	    cacheControl.setMustRevalidate(true);
		return cacheControl;
	}
}
