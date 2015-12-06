package net.sokontokoro_factory.lib.twitter;

import java.util.HashMap;

import net.sokontokoro_factory.lib.twitter.oauth.v1.AccessToken;
import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.lib.twitter.oauth.v1.RequestToken;
import net.sokontokoro_factory.lib.twitter.restapi.Executor;

public class Twitter {
	Authorization authorization;
		
	public Twitter(){

	}
	public Authorization getRequestToken(String oauth_callback){
		Authorization authorization = new Authorization();
		RequestToken requestToken = new RequestToken(authorization, oauth_callback);
		
		/* key & value of "oauth_token", "oauth_token_secret" and "oauth_callback_confirmed" in responseBody*/
		String responseBody = requestToken.request();
		authorization.setRequestTokenResponseBody(responseBody);
		return authorization;
	}
	
	public Authorization getAccessToken(String request_token, String request_token_secret, String oauth_verifier){
		Authorization authorization = new Authorization();
		authorization.setRequestToken(request_token);
		authorization.setRequestTokenSecret(request_token_secret);
		authorization.setOauthVerifier(oauth_verifier);
		AccessToken accessToken = new AccessToken(authorization);
		
		/* key & value of "oauth_token", "oauth_token_secret", "user_id" and "screen_name" in responseBody*/
		String responseBody = accessToken.request();
		authorization.setAccessTokenResponseBody(responseBody);
		return authorization;
	}
	public String getResource(
				String access_token, 
				String sccess_token_secret, 
				String apiUrl, 
				HashMap<String, String> parameterQuery){

		Executor exe = new Executor(authorization, apiUrl, parameterQuery);
		String resource = exe.get();
		
		return resource;
		
	}
}
