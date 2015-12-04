package net.sokontokoro_factory.lib.twitter.oauth.v1;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import net.sokontokoro_factory.lib.twitter.SPConnection;

public class AccessToken{
	
	private String signature;
	private String requestHeaderAuthorization;
	
	// アクセストークンを取得するservice providerのエンドポイント
	private String ENDPOINT = "https://api.twitter.com/oauth/access_token";
	private String REQUEST_METHOD = "POST";
	private String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
	private String OAUTH_VERSION = "1.0";

	
	public AccessToken(Authorization authorization){
		
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String oauth_nonce = UUID.randomUUID().toString();
		
		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("oauth_consumer_key", authorization.getOauthConsumerKey());
		element.put("oauth_nonce", oauth_nonce);
		element.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		element.put("oauth_timestamp", timestamp);
		element.put("oauth_token", authorization.getRequestToken());
		element.put("oauth_verifier", authorization.getOauthVerifier());
		element.put("oauth_version", OAUTH_VERSION);

		signature = Signature.generate(
						authorization.getOauthConsumerSecret(),
						authorization.getRequestTokenSecret(),
						REQUEST_METHOD,
						ENDPOINT,
						element);
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
	private String getRequestHeaderAuthorization(TreeMap<String,String> element){
		StringBuffer requestHeader = new StringBuffer();
		requestHeader.append("OAuth ");
		while (true){
			String mapKey = element.firstKey();
			requestHeader.append(mapKey);
			requestHeader.append("=");
			requestHeader.append(element.get(mapKey));
			element.remove(mapKey);
			if(element.isEmpty()) {
				break;
			}else{
				requestHeader.append(",");				
			}
		}
		return requestHeader.toString();
	}
}
