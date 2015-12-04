package net.sokontokoro_factory.lib.twitter.restapi;

import static org.junit.Assert.*;

import java.util.HashMap;

import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.lib.twitter.util.Config;

import org.junit.Test;

public class ExecutorTest {

	@Test
	public void 指定したuser情報を取得出来る() {
		Authorization authorization = new Authorization();
		authorization.setAccessToken(Config.getString("test.access.token"));
		authorization.setAccessTokenSecret(Config.getString("test.access.token.secret"));
		String apiUrl = "https://api.twitter.com/1.1/users/show.json";
		HashMap<String, String> parameterQuery = new HashMap<String, String>();
		parameterQuery.put("user_id", Config.getString("test.id"));
		Executor exe = new Executor(authorization, apiUrl, parameterQuery);
		String resource = exe.get();
		
		assertTrue(resource.contains(Config.getString("test.id")));
	}

}
