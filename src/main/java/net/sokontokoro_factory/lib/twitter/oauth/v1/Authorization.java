package net.sokontokoro_factory.lib.twitter.oauth.v1;

import net.sokontokoro_factory.lib.twitter.util.Config;

public class Authorization{

	private String oauth_consumer_key;
	private String oauth_consumer_secret;
	
	// リクエストトークン取得によるもの
	// リクエストトークン。ユーザーが認証画面にアクセスする時のパラメータに必要な値です。
	private String request_token;			// レスポンスボディでは、oauth_token
	// リクエストトークンシークレット。後ほどアクセストークンを取得する際に必要です。
	private String request_token_secret;	// レスポンスボディでは、oauth_token_secret
	private String oauth_callback_confirmed;

	// ユーザーの認証後に取得するもの
	private String oauth_verifier;
	
	// アクセストークン取得によるもの
	private String access_token;			// レスポンスボディでは、oauth_token
	private String access_token_secret;		// レスポンスボディでは、oauth_token_secret
	private String user_id;
	private String screen_name;
	
	public Authorization(){
		this.oauth_consumer_key = Config.getString("consumer.key");
		this.oauth_consumer_secret = Config.getString("consumer.secret");
	}
	public void setRequestTokenResponseBody(String responseBody){
		// TODO: HTTPレスポンスOKの時しかまだ実装してない
		// &を=に置き換えて、取得文字列を各要素に分割
		String paramaters = responseBody.replaceAll("&", "=");
		String[] paramater = paramaters.split("=");
		this.setRequestToken(paramater[1]);
		this.setRequestTokenSecret(paramater[3]);
		this.setOauthCallbackConfirmed(paramater[5]);
	}
	public void setAccessTokenResponseBody(String responseBody){
		// HTTPレスポンスOKの時しかまだ実装してない
		// &を=に置き換えて、取得文字列を各要素に分割
		String paramaters = responseBody.replaceAll("&", "=");
		String[] paramater = paramaters.split("=");
		
		this.setAccessToken(paramater[1]);
		this.setAccessTokenSecret(paramater[3]);
		this.setUserId(paramater[5]);
		this.setScreenName(paramater[7]);
	}
	
	/* The following is "getter" & "setter"*/
	public String getOauthConsumerKey(){
		return oauth_consumer_key;
	}
	public String getOauthConsumerSecret(){
		return oauth_consumer_secret;
	}
	public String getRequestToken() {
		return request_token;
	}
	public void setRequestToken(String request_token) {
		this.request_token = request_token;
	}
	public String getRequestTokenSecret() {
		return request_token_secret;
	}
	public void setRequestTokenSecret(String request_token_secret) {
		this.request_token_secret = request_token_secret;
	}
	public String getOauthCallbackConfirmed() {
		return oauth_callback_confirmed;
	}
	public void setOauthCallbackConfirmed(String oauth_callback_confirmed) {
		this.oauth_callback_confirmed = oauth_callback_confirmed;
	}
	public String getAccessToken() {
		return access_token;
	}
	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}
	public String getAccessTokenSecret() {
		return access_token_secret;
	}
	public void setAccessTokenSecret(String access_token_secret) {
		this.access_token_secret = access_token_secret;
	}
	public String getUserId() {
		return user_id;
	}
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	public String getScreenName() {
		return screen_name;
	}
	public void setScreenName(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getOauthVerifier() {
		return oauth_verifier;
	}
	public void setOauthVerifier(String oauth_verifier) {
		this.oauth_verifier = oauth_verifier;
	}
}
