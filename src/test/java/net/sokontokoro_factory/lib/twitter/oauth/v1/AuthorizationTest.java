package net.sokontokoro_factory.lib.twitter.oauth.v1;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class AuthorizationTest {

	@Test
	public void コンシューマーキーが取得出来る() {
		Authorization authorization = new Authorization();
		String actual = authorization.getOauthConsumerKey();
		assertThat(actual, is("DqMQV83OkYYNfhiepou491bAV"));
	}
	@Test
	public void コンシューマーシークレットが取得できる() {
		Authorization authorization = new Authorization();
		String actual = authorization.getOauthConsumerSecret();
		assertThat(actual, is("zlDtIRAetm0u3z8T9o2qB2cNDgv31qYEHyQyTPEWD0Q677cbSL"));
	}
}
