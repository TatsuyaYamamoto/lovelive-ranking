package net.sokontokoro_factory.lib.twitter;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class SPConnectionTest {

	// @Test
	public void post通信出来る() {
		String endpoint = "https://api.twitter.com/oauth/request_token";
		HashMap<String, String> requestHeaderMap = new HashMap<String, String>();
		requestHeaderMap.put("Authorization", "hoge");

		SPConnection connection = new SPConnection(endpoint, requestHeaderMap, "");
		String responseBody = connection.post();
		assertTrue(responseBody.contains("oauth_token"));
		assertTrue(responseBody.contains("oauth_token_secret"));
		assertTrue(responseBody.contains("oauth_callback_confirmed"));
	}

}
