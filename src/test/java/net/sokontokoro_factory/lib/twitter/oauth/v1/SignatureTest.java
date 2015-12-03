package net.sokontokoro_factory.lib.twitter.oauth.v1;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.TreeMap;

import net.sokontokoro_factory.lib.twitter.oauth.v1.Signature;
import net.sokontokoro_factory.lib.twitter.util.Config;

import org.junit.Test;

public class SignatureTest {
	
	private static final String SIGNATURE_BASE_STRING = "POST&https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token&oauth_consumer_key%3DDqMQV83OkYYNfhiepou491bAV%26oauth_nonce%3D40f52f9777c263b892bd164cf784d445%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1449072274%26oauth_version%3D1.0";
	private static final String REQUEST_METHOD = "POST";
	private static final String REQUEST_URI = "https://api.twitter.com/oauth/request_token";
	private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
	private static final String OAUTH_VERSION = "1.0";	
	
	@Test
	public void keyを作成する(){		
		String expected = Config.getString("consumer.secret") +"&"+ "";
		String actual = Signature.createKey(Config.getString("consumer.secret"), "");

		assertThat(actual, is(expected));
	}
	
	@Test
	public void dataを作成する(){		
		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("oauth_consumer_key", Config.getString("consumer.key"));
		element.put("oauth_nonce", "40f52f9777c263b892bd164cf784d445");
		element.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		element.put("oauth_timestamp", "1449072274");
		element.put("oauth_version", OAUTH_VERSION);
		
		String expected = SIGNATURE_BASE_STRING;
		String actual = Signature.createData(REQUEST_METHOD, REQUEST_URI, element);

		assertThat(actual, is(expected));
	}
	
	@Test
	public void keyとdataからsignature作成する() {
		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("oauth_consumer_key", Config.getString("consumer.key"));
		element.put("oauth_nonce", "40f52f9777c263b892bd164cf784d445");
		element.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		element.put("oauth_timestamp", "1449072274");
		element.put("oauth_version", OAUTH_VERSION);
		String signature = Signature.generate(
				Config.getString("consumer.secret"),
				"", // oauth_token_secretなし
				REQUEST_METHOD,
				REQUEST_URI,
				element);
		
		assertThat(signature ,is("%2Bu3oN56g84njqKOuPqkftefdeKY%3D"));
		
	}


}
