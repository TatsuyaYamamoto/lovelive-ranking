package net.sokontokoro_factory.lib.twitter.restapi;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.TreeMap;

import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.lib.twitter.util.Config;

import org.junit.Test;

public class ExecutorTest {
	
	@Test
	public void 指定したuser情報を取得出来る() {
		String apiUrl = "https://api.twitter.com/1.1/users/show.json";
		
		// setUp
		Authorization authorization = new Authorization();
		authorization.setAccessToken(Config.getString("test.access.token"));
		authorization.setAccessTokenSecret(Config.getString("test.access.token.secret"));

		HashMap<String, String> parameterQuery = new HashMap<String, String>();
		parameterQuery.put("user_id", Config.getString("test.id"));
		Executor exe = new Executor(authorization, apiUrl, parameterQuery);

		// execute
		String resource = exe.get();
		
		// check
		assertTrue(resource.contains(Config.getString("test.id")));
		System.out.println(resource);
	}
	
	@Test
	public void urlとパラメータからエンドポイントを作成する() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String apiUrl = "https://example.com/users.json";
		
		// setUp
		Authorization authorization = new Authorization();
		HashMap<String, String> parameterQuery = new HashMap<String, String>();
		parameterQuery.put("key1", "value1");
		parameterQuery.put("key2", "value2");
		Executor exe = new Executor(authorization, apiUrl, parameterQuery);
		
		// exe
		Method method = Executor.class.getDeclaredMethod("getEndpoint");
		method.setAccessible(true);
		String actual = (String)method.invoke(exe);
		
		// check
		assertThat(actual, is("https://example.com/users.json?key1=value1&key2=value2"));
		
		
	}
	
	@Test
	public void 認証用リクエストヘッダーを作成する() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		TreeMap<String, String> element = new TreeMap<String, String>();
		element.put("key2", "value2");
		element.put("key1", "value1");
		Executor exe = new Executor(null, null, null);
		
		Method method =Executor.class.getDeclaredMethod("getRequestHeaderAuthorization", TreeMap.class);
		method.setAccessible(true);
		String actual = (String)method.invoke(exe, element);
		
		assertThat(actual, is("OAuth key1=value1,key2=value2"));
		
	}

}
