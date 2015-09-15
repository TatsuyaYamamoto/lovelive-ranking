package net.sokontokoro_factory.games.oauth;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class TokenService {

	final static String OAUTH_CONSUMER_KEY = "KP3sQoU9wkwZeAMU5TPjy68Pv";
	final static String OAUTH_CONSUMER_SECRET = "bsik1rXRsWeunpb0gRAYJDwINI9CwwdTqPDwJXZGD8Bfzcpz92";
	final static String CALLBACK_TO_SERVER = "https://lit-taiga-3631.herokuapp.com/oauth/redirect";
	final static String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
	final static String OAUTH_VERSION = "1.0";
	final static String ENDPOINT_TWITTER_ACCESS_TOKEN = "https://api.twitter.com/oauth/access_token";
	final static String ENDPOINT_TWITTER_REQUEST_TOKEN = "https://api.twitter.com/oauth/request_token";



	protected static String getAccessToken(String oauth_token, String oauth_verifier) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String body = "";

		try {

			httpClient = HttpClients.createDefault();
			URI uri = new URI(ENDPOINT_TWITTER_ACCESS_TOKEN);
			HttpPost postMethod = new HttpPost(uri);

			long timestamp = System.currentTimeMillis() / 1000;
			String oauth_nonce = UUID.randomUUID().toString();
			
			
			String signature = getAccessSignature(oauth_nonce, timestamp,
					oauth_token, "POST", ENDPOINT_TWITTER_ACCESS_TOKEN);
			
			StringBuffer authorization = new StringBuffer();
			authorization.append("OAuth ");
			authorization.append("oauth_consumer_key=");
			authorization.append(OAUTH_CONSUMER_KEY);
			authorization.append(", oauth_nonce=");
			authorization.append(oauth_nonce);
			authorization.append(", oauth_signature=");
			authorization.append(signature);
			authorization.append(", oauth_signature_method=");
			authorization.append(OAUTH_SIGNATURE_METHOD);
			authorization.append(", oauth_timestamp=");
			authorization.append(timestamp);
			authorization.append(", oauth_token=");
			authorization.append(oauth_token);
			authorization.append(", oauth_verifier=");
			authorization.append(oauth_verifier);
			authorization.append(", oauth_version=");
			authorization.append(OAUTH_VERSION);

			postMethod.setHeader("Authorization", authorization.toString());

			response = httpClient.execute(postMethod);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return body;

	}

	private static String getAccessSignature(
				String oauth_nonce, 
				long timestamp,
				String oauth_token, 
				String requestMethod, 
				String requestURL) {

		String oauth_token_secret = "";

		// キーの作成
		String key = OAUTH_CONSUMER_SECRET + "&" + oauth_token_secret;

		// データの作成

		// パラメータ
		StringBuffer parameter = new StringBuffer();
		parameter.append("&oauth_consumer_key=");
		parameter.append(OAUTH_CONSUMER_KEY);
		parameter.append("&oauth_nonce=");
		parameter.append(oauth_nonce);
		parameter.append("&oauth_signature_method=");
		parameter.append(OAUTH_SIGNATURE_METHOD);
		parameter.append("&oauth_timestamp=");
		parameter.append(timestamp);
		parameter.append("&oauth_token=");
		parameter.append(oauth_token);
		parameter.append("&oauth_version=");
		parameter.append(OAUTH_VERSION);

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
			URI uri = new URI(ENDPOINT_TWITTER_REQUEST_TOKEN);
			HttpPost postMethod = new HttpPost(uri);

			long timestamp = System.currentTimeMillis() / 1000;
			String oauth_nonce = UUID.randomUUID().toString();
			String signature = getRequestSignature(CALLBACK_TO_SERVER, oauth_nonce,
					timestamp, "POST", ENDPOINT_TWITTER_REQUEST_TOKEN);

			// request header作成
			StringBuffer authorization = new StringBuffer();
			authorization.append("OAuth ");
			authorization.append("oauth_callback=");
			authorization.append(CALLBACK_TO_SERVER);
			authorization.append(", oauth_consumer_key=");
			authorization.append(OAUTH_CONSUMER_KEY);
			authorization.append(", oauth_nonce=");
			authorization.append(oauth_nonce);
			authorization.append(", oauth_signature=");
			authorization.append(signature);
			authorization.append(", oauth_signature_method=");
			authorization.append(OAUTH_SIGNATURE_METHOD);
			authorization.append(", oauth_timestamp=");
			authorization.append(timestamp);
			authorization.append(", oauth_version=");
			authorization.append(OAUTH_VERSION);

			postMethod.setHeader("Authorization", authorization.toString());
			response = httpClient.execute(postMethod);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return body;

	}

	protected static String getProfile(String user_id, String access_token, String accessa_token_secret) throws Exception {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String body = null;

		try {

			httpClient = HttpClients.createDefault();
			URI uri = new URI(
					"https://api.twitter.com/1.1/users/show.json?user_id="+user_id);
			HttpGet getMethod = new HttpGet(uri);

			long timestamp = System.currentTimeMillis() / 1000;
			String oauth_nonce = UUID.randomUUID().toString();
			String signature = getAPISignature(
					access_token,
					accessa_token_secret,
					oauth_nonce, timestamp, "GET",
					"https://api.twitter.com/1.1/users/show.json",
					"user_id="+user_id);
			StringBuffer authorization = new StringBuffer();
			authorization.append("OAuth ");
			authorization.append("oauth_consumer_key=");
			authorization.append(OAUTH_CONSUMER_KEY);
			authorization.append(", oauth_nonce=");
			authorization.append(oauth_nonce);
			authorization.append(", oauth_signature=");
			authorization.append(signature);
			authorization.append(", oauth_signature_method=");
			authorization.append(OAUTH_SIGNATURE_METHOD);
			authorization.append(", oauth_timestamp=");
			authorization.append(timestamp);
			authorization.append(", oauth_token=");
			authorization
					.append(access_token);
			authorization.append(", oauth_version=");
			authorization.append(OAUTH_VERSION);

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

	private static String getRequestSignature(String oauth_callback,
			String oauth_nonce, long timestamp, String requestMethod,
			String requestURL) {

		// キーの作成
		// oauth_token_secretは空文字可
		String key = OAUTH_CONSUMER_SECRET + "&";

		// データの作成

		// パラメータ
		StringBuffer parameter = new StringBuffer();
		parameter.append("oauth_callback=");
		parameter.append(oauth_callback);
		parameter.append("&oauth_consumer_key=");
		parameter.append(OAUTH_CONSUMER_KEY);
		parameter.append("&oauth_nonce=");
		parameter.append(oauth_nonce);
		parameter.append("&oauth_signature_method=");
		parameter.append(OAUTH_SIGNATURE_METHOD);
		parameter.append("&oauth_timestamp=");
		parameter.append(timestamp);
		parameter.append("&oauth_version=");
		parameter.append(OAUTH_VERSION);

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
			String signature_base64 = Base64.getEncoder().encodeToString(signature_sha1);
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
		String key = OAUTH_CONSUMER_SECRET + "&" + oauth_token_secret;

		// データの作成

		// パラメータ
		StringBuffer parameter = new StringBuffer();
		parameter.append("oauth_consumer_key=");
		parameter.append(OAUTH_CONSUMER_KEY);
		parameter.append("&oauth_nonce=");
		parameter.append(oauth_nonce);
		parameter.append("&oauth_signature_method=");
		parameter.append(OAUTH_SIGNATURE_METHOD);
		parameter.append("&oauth_timestamp=");
		parameter.append(timestamp);
		parameter.append("&oauth_token=");
		parameter.append(oauth_token);
		parameter.append("&oauth_version=");
		parameter.append(OAUTH_VERSION);
		parameter.append("&");
		parameter.append(requestQuery);// has Key and Value

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
			String signature_base64 = Base64.getEncoder().encodeToString(signature_sha1);
			signature = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (Exception e) {

		}

		return signature;

	}

	private static byte[] calculateHmacSHA1(String data, String key)
			throws java.security.SignatureException {

		byte[] rawHmac = null;
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(),
					"HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(secretKey);
			rawHmac = mac.doFinal(data.getBytes());
		} catch (Exception e) {
			// throw new SignatureException("Failed to generate HMAC : " +
			// e.getMessage());
		}
		return rawHmac;
	}

}
