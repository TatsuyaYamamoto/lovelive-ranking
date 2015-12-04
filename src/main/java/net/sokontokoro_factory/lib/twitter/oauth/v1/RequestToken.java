package net.sokontokoro_factory.lib.twitter.oauth.v1;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import net.sokontokoro_factory.lib.twitter.SPConnection;

public class RequestToken{

	private String requestHeaderAuthorization;
	
	// リクエストトークンを取得するservice providerのエンドポイント
	private static final String ENDPOINT = "https://api.twitter.com/oauth/request_token";	
	private static final String REQUESST_METHOD = "POST";
	private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
	private static final String OAUTH_VERSION = "1.0";

	public RequestToken(Authorization authorization, String oauth_callback){
		
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String oauth_nonce = UUID.randomUUID().toString();
		/*
		OAuth oauth_consumer_key=DqMQV83OkYYNfhiepou491bAV,
		oauth_nonce=40f52f9777c263b892bd164cf784d445, 
		oauth_signature=%2Bu3oN56g84njqKOuPqkftefdeKY%3D, oauth_signature_method=HMAC-SHA1, oauth_timestamp=1449072274,
				oauth_version=1.0
		*/
		// 署名作成に必要な要素
		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("oauth_callback", oauth_callback);
		element.put("oauth_consumer_key", authorization.getOauthConsumerKey());
		element.put("oauth_nonce", oauth_nonce);
		element.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		element.put("oauth_timestamp", timestamp);
		element.put("oauth_version", OAUTH_VERSION);
		
		String signature = Signature.generate(
				authorization.getOauthConsumerSecret(),
				"", // oauth_token_secretなし
				REQUESST_METHOD,
				ENDPOINT,
				element);
		
		// リクエストヘッドにはsignatureをさらに追加する
		element.put("oauth_signature", signature);
		requestHeaderAuthorization = getRequestHeaderAuthorization(element);
	}


	public String request(){
		HashMap<String, String> requestHeaderMap = new HashMap<String, String>();
		requestHeaderMap.put("Authorization", requestHeaderAuthorization);
		SPConnection connection = new SPConnection(ENDPOINT, requestHeaderMap, "");
		String responseBody = connection.post();
		return responseBody;
	}
	
	public String getRequestHeaderAuthorization(TreeMap<String,String> element){
		StringBuffer requestHeader = new StringBuffer();		
		int i = 0;
		for(String key: element.keySet()){
			if(i==0){
				requestHeader.append("OAuth ");
			}else{
				requestHeader.append(",");
			}
			requestHeader.append(key);
			requestHeader.append("=");
			requestHeader.append(element.get(key));
			i++;
		}
		return requestHeader.toString();
	}
}
