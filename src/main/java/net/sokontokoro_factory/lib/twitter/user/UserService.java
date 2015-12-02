package net.sokontokoro_factory.lib.twitter.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.sokontokoro_factory.api.util.Property;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

class UserService{

	protected static String execute(
								String access_token, 
								String accessa_token_secret, 
								String method, 
								String endpoint, 
								Map<String, String> param_query) 
								throws Exception {
		
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String result = null;

		// 実行するURIを作成
		StringBuffer endpoint_query = new StringBuffer();
		// エンドポイント
		endpoint_query.append(endpoint);
		// パラメータクエリ
		endpoint_query.append("?");
		for (Map.Entry<String, String> entry : param_query.entrySet()) {
			endpoint_query.append(entry.getKey());
			endpoint_query.append("=");
			endpoint_query.append(entry.getValue());
			endpoint_query.append("&");
		}
		// 最後の"&"を削除する
		// TODO 改良できるはず、、、、
		endpoint_query.deleteCharAt(endpoint_query.lastIndexOf("")-1);
		
		
		try {

			// 必要なもの
			String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
			String oauth_nonce = UUID.randomUUID().toString();

			// signature
			String signature = getSignature(
										access_token,
										accessa_token_secret,
										oauth_nonce, timestamp, 
										method,
										endpoint,
										param_query);
			
			// header
			String authorization = getAuthorization(
												oauth_nonce, 
												signature, 
												timestamp, 
												access_token);

			// Http request
			httpClient = HttpClients.createDefault();
			URI uri = new URI(endpoint_query.toString());

			if(method.equals("GET")){
				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("Authorization", authorization.toString());
				response = httpClient.execute(httpGet);
			}else if(method.equals("POST")){
				HttpPost httpPost = new HttpPost(uri);
				httpPost.setHeader("Authorization", authorization.toString());
				response = httpClient.execute(httpPost);
			}else{
				// bad request
			}
			
			result = EntityUtils.toString(response.getEntity(), "UTF-8");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return result;
	}

	private static String getAuthorization(
										String oauth_nonce, 
										String signature, 
										String timestamp, 
										String access_token){

		StringBuffer authorization = new StringBuffer();
		try{
			authorization
			.append("OAuth ")
			.append("oauth_consumer_key=")
			.append(Property.CONSUMER_KEY())
			.append(", oauth_nonce=")
			.append(oauth_nonce)
			.append(", oauth_signature=")
			.append(signature)
			.append(", oauth_signature_method=")
			.append(Property.SIGNATURE_METHOD())
			.append(", oauth_timestamp=")
			.append(timestamp)
			.append(", oauth_token=")
			.append(access_token)
			.append(", oauth_version=")
			.append(Property.SIGNATURE_VERSION());			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return authorization.toString();
	}

	private static String getSignature(
									String oauth_token,
									String access_token_secret,
									String oauth_nonce, 
									String timestamp, 
									String httpMethod, 
									String endpoint,
									Map<String, String> param_query){
	
	
	
		/* 1. キーの作成 */
		String key = null;

		/* 2. データの作成 */


		// パラメータ
		Map<String, String> elements = new TreeMap<String, String>();
		
		
		try{
			key = Property.CONSUMER_SECRET() + "&" + access_token_secret;
			
			elements.put("oauth_consumer_key", Property.CONSUMER_KEY());
			elements.put("oauth_nonce", oauth_nonce);
			elements.put("oauth_signature_method", Property.SIGNATURE_METHOD());
			elements.put("oauth_timestamp", timestamp);
			elements.put("oauth_token", oauth_token);
			elements.put("oauth_version", Property.SIGNATURE_VERSION());			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// param_queryをelementsに格納する
		for (Map.Entry<String, String> entry : param_query.entrySet()) {
			elements.put(entry.getKey(), entry.getValue());
		}

		// パラメータ(文字列)作成
		StringBuffer parameter = new StringBuffer();
		for (Map.Entry<String, String> entry : elements.entrySet()) {
			parameter
				.append(entry.getKey())
				.append("=")
				.append(entry.getValue())
				.append("&");
		}
		// 最後の"&"を削除する
		// TODO 改良できるはず、、、、
		parameter.deleteCharAt(parameter.lastIndexOf("")-1);


		// // パラメータ
		// StringBuffer parameter = new StringBuffer();
		// parameter.append("oauth_consumer_key=");
		// parameter.append(oauth_consumer_key);
		// parameter.append("&oauth_nonce=");
		// parameter.append(oauth_nonce);
		// parameter.append("&oauth_signature_method=");
		// parameter.append(oauth_signature_method);
		// parameter.append("&oauth_timestamp=");
		// parameter.append(timestamp);
		// parameter.append("&oauth_token=");
		// parameter.append(oauth_token);
		// parameter.append("&oauth_version=");
		// parameter.append(oauth_version);
		// parameter.append("&");
		// parameter.append(requestQuery);// has Key and Value

		// メソッド、リクエストURL、パラメータを&で繋げる

		String signature = null;
		try {
			// data作成
			StringBuffer data = new StringBuffer();
			data.append(URLEncoder.encode(httpMethod, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(endpoint, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(parameter.toString(), "UTF-8"));

			// keyとdataでsignatureを作成する
			byte[] signature_sha1 = calculateHmacSHA1(data.toString(), key);
			String signature_base64 = Base64.getEncoder().encodeToString(signature_sha1);
			signature = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (NoSuchAlgorithmException ex) {
			// ex.getMessage();
		} catch(InvalidKeyException ex){
			// ex.getMessage();
		} catch(UnsupportedEncodingException ex){
			// ex.getMessage();
		}

		return signature;

	}

	private static byte[] calculateHmacSHA1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {

		byte[] rawHmac = null;
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(secretKey);
		rawHmac = mac.doFinal(data.getBytes());
		return rawHmac;
	}
}
