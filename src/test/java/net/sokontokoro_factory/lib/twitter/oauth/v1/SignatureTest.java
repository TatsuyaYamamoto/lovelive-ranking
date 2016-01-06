package net.sokontokoro_factory.lib.twitter.oauth.v1;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.TreeMap;

import net.sokontokoro_factory.lib.twitter.oauth.v1.Signature;
import net.sokontokoro_factory.lib.twitter.util.Config;

import org.junit.Test;

public class SignatureTest {
	
	private static final String SIGNATURE_BASE_STRING = "GET&https%3A%2F%2Fapi.twitter.com%2F1.1%2F&oauth_consumer_key%3DDqMQV83OkYYNfhiepou491bAV%26oauth_nonce%3Def618b29c6bab631237e7ea30119f83c%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1452090573%26oauth_version%3D1.0";
	private static final String REQUEST_METHOD = "GET";
	private static final String REQUEST_URI = "https://api.twitter.com/1.1/";
	private static final String OAUTH_NONCE = "ef618b29c6bab631237e7ea30119f83c";
	private static final String OAUTH_TIMESTAMP = "1452090573";
	private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
	private static final String OAUTH_VERSION = "1.0";
	private static final String EXPECTED_SIGNATURE = "RZb3C6cKwSzW1Ll56Lp%2F4id0%2FE0%3D";
	
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
		element.put("oauth_nonce", OAUTH_NONCE);
		element.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		element.put("oauth_timestamp", OAUTH_TIMESTAMP);
		element.put("oauth_version", OAUTH_VERSION);
		
		String expected = SIGNATURE_BASE_STRING;
		String actual = Signature.createData(
									REQUEST_METHOD, 
									REQUEST_URI, 
									element);

		assertThat(actual, is(expected));
	}
	
//	@Test
	public void keyとdataからsignature作成する() {
		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("oauth_consumer_key", Config.getString("consumer.key"));
		element.put("oauth_nonce", OAUTH_NONCE);
		element.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		element.put("oauth_timestamp", OAUTH_TIMESTAMP);
		element.put("oauth_version", OAUTH_VERSION);
		String signature = Signature.generate(
				Config.getString("consumer.secret"),
				"", // oauth_token_secretなし
				REQUEST_METHOD,
				REQUEST_URI,
				element);
		
		assertThat(signature ,is(EXPECTED_SIGNATURE));
		
	}


}
