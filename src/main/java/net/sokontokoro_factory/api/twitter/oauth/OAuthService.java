package net.sokontokoro_factory.api.twitter.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.net.URI;
import java.net.URLEncoder;
import java.util.UUID;

import net.sokontokoro_factory.api.util.Property;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OAuthService {

	protected static String getAccessToken(String oauth_token,
			String oauth_verifier) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String body = "";

		try {

			httpClient = HttpClients.createDefault();

			URI uri = new URI(Property.ACCESS_TOKEN_URL());
			HttpPost postMethod = new HttpPost(uri);

			long timestamp = System.currentTimeMillis() / 1000;
			String oauth_nonce = UUID.randomUUID().toString();

			String signature = getAccessSignature(
												oauth_nonce, 
												timestamp,
												oauth_token, 
												"POST", 
												Property.ACCESS_TOKEN_URL());

			StringBuffer authorization = new StringBuffer();
			authorization.append("OAuth ");
			authorization.append("oauth_consumer_key=");
			authorization.append(Property.CONSUMER_KEY());
			authorization.append(", oauth_nonce=");
			authorization.append(oauth_nonce);
			authorization.append(", oauth_signature=");
			authorization.append(signature);
			authorization.append(", oauth_signature_method=");
			authorization.append(Property.SIGNATURE_METHOD());
			authorization.append(", oauth_timestamp=");
			authorization.append(timestamp);
			authorization.append(", oauth_token=");
			authorization.append(oauth_token);
			authorization.append(", oauth_verifier=");
			authorization.append(oauth_verifier);
			authorization.append(", oauth_version=");
			authorization.append(Property.SIGNATURE_VERSION());				



			postMethod.setHeader("Authorization", authorization.toString());

			response = httpClient.execute(postMethod);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return body;

	}

	private static String getAccessSignature(String oauth_nonce,
			long timestamp, String oauth_token, String requestMethod,
			String requestURL) {

		String oauth_token_secret = "";

		// キーの作成
		String key = null;
		// データの作成
		StringBuffer parameter = new StringBuffer();
		try{
			key = Property.CONSUMER_SECRET() + "&" + oauth_token_secret;
			
			parameter.append("&oauth_consumer_key=");
			parameter.append(Property.CONSUMER_KEY());
			parameter.append("&oauth_nonce=");
			parameter.append(oauth_nonce);
			parameter.append("&oauth_signature_method=");
			parameter.append(Property.SIGNATURE_METHOD());
			parameter.append("&oauth_timestamp=");
			parameter.append(timestamp);
			parameter.append("&oauth_token=");
			parameter.append(oauth_token);
			parameter.append("&oauth_version=");
			parameter.append(Property.SIGNATURE_VERSION());	
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuffer data = new StringBuffer();
		try {
			data.append(URLEncoder.encode(requestMethod, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(requestURL, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(parameter.toString(), "UTF-8"));
		} catch (Exception e) {
		}
		byte[] signature_sha1 = null;
		try {
			signature_sha1 = calculateHmacSHA1(data.toString(), key);

		} catch (Exception e) {

		}
		String signature_base64 = Base64.getEncoder().encodeToString(
				signature_sha1);

		String signature_URI = null;
		try {
			signature_URI = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (Exception e) {

		}

		return signature_URI;

	}

	protected static String getRequestToken() {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String body = "";

		try {

			httpClient = HttpClients.createDefault();
			URI uri = new URI(Property.REQUEST_TOKEN_URL());
			HttpPost postMethod = new HttpPost(uri);

			long timestamp = System.currentTimeMillis() / 1000;
			String oauth_nonce = UUID.randomUUID().toString();
			String signature = getRequestSignature(
												Property.SERVER_ORIGIN() + "/api/twitter/oauth/callback",
												oauth_nonce, 
												timestamp, 
												"POST",
												Property.REQUEST_TOKEN_URL());

			// request header作成
			StringBuffer authorization = new StringBuffer();
			

			authorization.append("OAuth ");
			authorization.append("oauth_callback=");
			authorization.append(Property.SERVER_ORIGIN() + "/api/twitter/oauth/callback");
			authorization.append(", oauth_consumer_key=");
			authorization.append(Property.CONSUMER_KEY());
			authorization.append(", oauth_nonce=");
			authorization.append(oauth_nonce);
			authorization.append(", oauth_signature=");
			authorization.append(signature);
			authorization.append(", oauth_signature_method=");
			authorization.append(Property.SIGNATURE_METHOD());
			authorization.append(", oauth_timestamp=");
			authorization.append(timestamp);
			authorization.append(", oauth_version=");
			authorization.append(Property.SIGNATURE_VERSION());				



			postMethod.setHeader("Authorization", authorization.toString());
			response = httpClient.execute(postMethod);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return body;

	}

	protected static String getProfile(
									String user_id, 
									String access_token,
									String accessa_token_secret) 
									throws Exception {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String body = null;

		try {

			httpClient = HttpClients.createDefault();
			URI uri = new URI(
					"https://api.twitter.com/1.1/users/show.json?user_id="
							+ user_id);
			HttpGet getMethod = new HttpGet(uri);

			long timestamp = System.currentTimeMillis() / 1000;
			String oauth_nonce = UUID.randomUUID().toString();
			String signature = getAPISignature(
											access_token,
											accessa_token_secret, 
											oauth_nonce, 
											timestamp, 
											"GET",
											"https://api.twitter.com/1.1/users/show.json", 
											"user_id=" + user_id);
			StringBuffer authorization = new StringBuffer();
			
			try{
				authorization.append("OAuth ");
				authorization.append("oauth_consumer_key=");
				authorization.append(Property.CONSUMER_KEY());
				authorization.append(", oauth_nonce=");
				authorization.append(oauth_nonce);
				authorization.append(", oauth_signature=");
				authorization.append(signature);
				authorization.append(", oauth_signature_method=");
				authorization.append(Property.SIGNATURE_METHOD());
				authorization.append(", oauth_timestamp=");
				authorization.append(timestamp);
				authorization.append(", oauth_token=");
				authorization.append(access_token);
				authorization.append(", oauth_version=");
				authorization.append(Property.SIGNATURE_VERSION());
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			getMethod.setHeader("Authorization", authorization.toString());

			response = httpClient.execute(getMethod);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return body;
	}

	private static String getRequestSignature(
											String oauth_callback,
											String oauth_nonce, 
											long timestamp, 
											String requestMethod,
											String requestURL) {

		// キーの作成
		// oauth_token_secretは空文字可
		String key = null;

		// データの作成

		// パラメータ
		StringBuffer parameter = new StringBuffer();

		try {
			key = Property.CONSUMER_SECRET() + "&";
			parameter.append("oauth_callback=");
			parameter.append(oauth_callback);
			parameter.append("&oauth_consumer_key=");
			parameter.append(Property.CONSUMER_KEY());
			parameter.append("&oauth_nonce=");
			parameter.append(oauth_nonce);
			parameter.append("&oauth_signature_method=");
			parameter.append(Property.SIGNATURE_METHOD());
			parameter.append("&oauth_timestamp=");
			parameter.append(timestamp);
			parameter.append("&oauth_version=");
			parameter.append(Property.SIGNATURE_VERSION());
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		StringBuffer data = new StringBuffer();
		try {
			data.append(URLEncoder.encode(requestMethod, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(requestURL, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(parameter.toString(), "UTF-8"));
		} catch (Exception e) {
		}

		String signature_URI = null;
		try {
			byte[] signature_sha1 = calculateHmacSHA1(data.toString(), key);
			String signature_base64 = Base64.getEncoder().encodeToString(
					signature_sha1);
			signature_URI = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (Exception e) {

		}

		return signature_URI;

	}

	private static String getAPISignature(String oauth_token,
			String oauth_token_secret, String oauth_nonce, long timestamp,
			String requestMethod, String requestURL, String requestQuery) {

		// キーの作成
		// oauth_token_secretは空文字可
		String key = null;

		// データの作成

		// パラメータ
		StringBuffer parameter = new StringBuffer();
		try{
			key = Property.CONSUMER_SECRET() + "&" + oauth_token_secret;
			
			parameter.append("oauth_consumer_key=");
			parameter.append(Property.CONSUMER_KEY());
			parameter.append("&oauth_nonce=");
			parameter.append(oauth_nonce);
			parameter.append("&oauth_signature_method=");
			parameter.append(Property.SIGNATURE_METHOD());
			parameter.append("&oauth_timestamp=");
			parameter.append(timestamp);
			parameter.append("&oauth_token=");
			parameter.append(oauth_token);
			parameter.append("&oauth_version=");
			parameter.append(Property.SIGNATURE_VERSION());
			parameter.append("&");
			parameter.append(requestQuery);// has Key and Value	
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer data = new StringBuffer();
		try {
			data.append(URLEncoder.encode(requestMethod, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(requestURL, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(parameter.toString(), "UTF-8"));
		} catch (Exception e) {
		}
		String signature = null;
		try {
			byte[] signature_sha1 = calculateHmacSHA1(data.toString(), key);
			String signature_base64 = Base64.getEncoder().encodeToString(
					signature_sha1);
			signature = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (Exception e) {

		}

		return signature;

	}

	private static String getAuthorization(
										String oauth_nonce,
										String signature, 
										String timestamp, 
										String access_token) {

		StringBuffer authorization = new StringBuffer();
		try{
			authorization.append("OAuth ")
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
									Map<String, String> param_query) {

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
			parameter.append(entry.getKey()).append("=")
					.append(entry.getValue()).append("&");
		}
		// 最後の"&"を削除する
		// TODO 改良できるはず、、、、
		parameter.deleteCharAt(parameter.lastIndexOf("") - 1);

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
			String signature_base64 = Base64.getEncoder().encodeToString(
					signature_sha1);
			signature = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (NoSuchAlgorithmException ex) {
			// ex.getMessage();
		} catch (InvalidKeyException ex) {
			// ex.getMessage();
		} catch (UnsupportedEncodingException ex) {
			// ex.getMessage();
		}

		return signature;

	}

	private static byte[] calculateHmacSHA1(String data, String key)
			throws NoSuchAlgorithmException, InvalidKeyException {

		byte[] rawHmac = null;
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(secretKey);
		rawHmac = mac.doFinal(data.getBytes());
		return rawHmac;
	}
}
