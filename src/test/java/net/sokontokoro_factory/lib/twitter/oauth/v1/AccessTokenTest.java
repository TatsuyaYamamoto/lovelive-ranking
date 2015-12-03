package net.sokontokoro_factory.lib.twitter.oauth.v1;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import net.sokontokoro_factory.api.util.Property;
import net.sokontokoro_factory.lib.twitter.oauth.v1.AccessToken;
import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.lib.twitter.oauth.v1.RequestToken;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccessTokenTest {


	public void アクセストークンを取得出来る() throws ConfigurationException {
		Authorization authorization = new Authorization();
		RequestToken requestToken = new RequestToken(authorization, Property.SERVER_ORIGIN() + "/v1/auth/twitter/callback");
		
		/* key & value of "oauth_token", "oauth_token_secret" and "oauth_callback_confirmed" in responseBody*/
		String responseBody = requestToken.request();
		authorization.setRequestTokenResponseBody(responseBody);

		AccessToken accessToken = new AccessToken(authorization);
		
		/* key & value of "oauth_token", "oauth_token_secret", "user_id" and "screen_name" in responseBody*/
		String responseBody2 = accessToken.request();
		assertThat(responseBody2, is("hoge"));

	}

}
