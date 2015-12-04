package net.sokontokoro_factory.lib.twitter.restapi;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import net.sokontokoro_factory.lib.twitter.SPConnection;
import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.lib.twitter.oauth.v1.Signature;
import net.sokontokoro_factory.lib.twitter.util.Config;

public class Executor {
	
	private Authorization authorization;
	private String apiUrl;
	private HashMap<String, String> parameterQuery;

	public Executor(Authorization authorization, String apiUrl, HashMap<String, String> parameterQuery){
		this.authorization = authorization;
		this.apiUrl = apiUrl;
		this.parameterQuery = parameterQuery;
	}
	public String get(){
		// エンドポイントの作成
		String endpoint = getEndpoint();
				
		// 署名作成
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String oauth_nonce = UUID.randomUUID().toString();
		
		TreeMap<String,String> element = new TreeMap<String,String>();
		element.put("oauth_consumer_key", Config.getString("consumer.key"));
		element.put("oauth_nonce", oauth_nonce);
		element.put("oauth_signature_method", "HMAC-SHA1");
		element.put("oauth_timestamp", timestamp);
		element.put("oauth_token", authorization.getAccessToken());
		element.put("oauth_version", "1.0");
		element.putAll(parameterQuery);

		String signature = Signature.generate(
						Config.getString("consumer.secret"),
						authorization.getAccessTokenSecret(), 
						"GET",
						apiUrl,
						element);

		// リクエストヘッダー(value)作成
		element.put("oauth_signature", signature);
		element.remove("user_id");
		String requestHeaderAuthorization = getRequestHeaderAuthorization(element);

		// リクエストヘッダを作成
		HashMap<String, String> requestHeaderMap = new HashMap<String, String>();
		requestHeaderMap.put("Authorization", requestHeaderAuthorization);
		// 実行
		SPConnection connection = new SPConnection(endpoint.toString(), requestHeaderMap ,"");
		return connection.get();
	}
	private String getEndpoint(){
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(apiUrl);
		int i = 0;
		for(String key: parameterQuery.keySet()){
			if(i==0){
				endpoint.append("?");
			}else{
				endpoint.append("&");
			}
			endpoint.append(key);
			endpoint.append("=");
			endpoint.append(parameterQuery.get(key));
			i++;
		}
		return endpoint.toString();
	}
	private String getRequestHeaderAuthorization(TreeMap<String,String> element){
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
