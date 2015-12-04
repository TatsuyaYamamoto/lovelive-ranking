package net.sokontokoro_factory.lib.twitter;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

public class SPConnectionTest {

	// @Test
	public void post通信出来る() {
		String endpoint = "https://api.twitter.com/oauth/request_token";
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String oauth_nonce = UUID.randomUUID().toString();
		
		String header = "OAuth oauth_consumer_key=DqMQV83OkYYNfhiepou491bAV, oauth_nonce=" + oauth_nonce + ", oauth_signature=%2Bu3oN56g84njqKOuPqkftefdeKY%3D, oauth_signature_method=HMAC-SHA1, oauth_timestamp=" + timestamp + ", oauth_version=1.0";
		SPConnection connection = new SPConnection(endpoint, header, "");
		String responseBody = connection.post();
		assertTrue(responseBody.contains("oauth_token"));
		assertTrue(responseBody.contains("oauth_token_secret"));
		assertTrue(responseBody.contains("oauth_callback_confirmed"));
	}

}
