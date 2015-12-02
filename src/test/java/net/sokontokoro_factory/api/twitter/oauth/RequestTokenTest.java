package net.sokontokoro_factory.api.twitter.oauth;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.TreeMap;

import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.lib.twitter.oauth.v1.RequestToken;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class RequestTokenTest {

	@Test
	public void リクエストトークンを取得出来る() throws ConfigurationException{
		Authorization authorization = new Authorization();
		RequestToken requestToken = new RequestToken(authorization, "http://ec2-54-65-78-59.ap-northeast-1.compute.amazonaws.com:8080http://ec2-54-65-78-59.ap-northeast-1.compute.amazonaws.com:8080/v1/auth/twitter/callback");
		
		String responseBody = requestToken.request();
		assertTrue(responseBody.contains("oauth_token"));
		assertTrue(responseBody.contains("oauth_token_secret"));
		assertTrue(responseBody.contains("oauth_callback_confirmed"));
	}
	
	@Test
	public void リクエストヘッダを作成できる(){
		Authorization authorization = new Authorization();
		RequestToken requestToken = new RequestToken(authorization, "http://ec2-54-65-78-59.ap-northeast-1.compute.amazonaws.com:8080http://ec2-54-65-78-59.ap-northeast-1.compute.amazonaws.com:8080/v1/auth/twitter/callback");

		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("hoge", "HOGE");
		element.put("piyo", "PIYO");
		element.put("fuga", "FUGA");
		
		String actual = requestToken .getRequestHeaderAuthorization(element);
		assertThat(actual, is("OAuth fuga=FUGA,hoge=HOGE,piyo=PIYO"));
	}
}
