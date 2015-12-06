package net.sokontokoro_factory.lib.twitter.oauth.v1;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import net.sokontokoro_factory.lib.twitter.util.Config;

import org.junit.Test;

public class AuthorizationTest {

	@Test
	public void コンシューマーキーが取得出来る() {
		Authorization authorization = new Authorization();
		String actual = authorization.getOauthConsumerKey();
		String expected = Config.getString("consumer.key");
		assertThat(actual, is(expected));
	}
	@Test
	public void コンシューマーシークレットが取得できる() {
		Authorization authorization = new Authorization();
		String actual = authorization.getOauthConsumerSecret();
		String expected = Config.getString("consumer.secret");
		assertThat(actual, is(expected));
	}
}
