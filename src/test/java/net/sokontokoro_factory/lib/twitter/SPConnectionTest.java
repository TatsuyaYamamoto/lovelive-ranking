package net.sokontokoro_factory.lib.twitter;

import static org.junit.Assert.*;

import org.junit.Test;

public class SPConnectionTest {

	@Test
	public void post通信出来る() {
		String endpoint = "https://api.twitter.com/oauth/request_token";
		String header = "OAuth oauth_consumer_key=DqMQV83OkYYNfhiepou491bAV, oauth_nonce=40f52f9777c263b892bd164cf784d445, oauth_signature=%2Bu3oN56g84njqKOuPqkftefdeKY%3D, oauth_signature_method=HMAC-SHA1, oauth_timestamp=1449072274, oauth_version=1.0";
		SPConnection connection = new SPConnection(endpoint, header, "");
		String responseBody = connection.post();
		assertTrue(responseBody.contains("oauth_token"));
		assertTrue(responseBody.contains("oauth_token_secret"));
		assertTrue(responseBody.contains("oauth_callback_confirmed"));
	}

}
