package net.sokontokoro_factory.api.twitter.util;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Signature{

	
	final static String OAUTH_CONSUMER_SECRET = "bsik1rXRsWeunpb0gRAYJDwINI9CwwdTqPDwJXZGD8Bfzcpz92";
	
//	public static String get(
//			String oauth_consumer_secret, 
//			String oauth_token_secret,
//			String oauth_consumer_key, 
//			String oauth_nonce, 
//			String oauth_signature_method, 
//			long timestamp,
//			String oauth_token,
//			String oauth_version, 
//			String requestMethod, 
//			String requestURL, 
//			String requestQuery) {

//	public static String get(
//			String access_token,
//			String access_token_secret,
//			String oauth_nonce, 
//			String timestamp, 
//			String method,
//			String endpoint,
//			HashMap<String, String> param_query){
//	
//	
//	
//		/* 1. キーの作成 */
//		// oauth_token_secretは空文字可
//		String key = OAUTH_CONSUMER_SECRET + "&" + access_token_secret;
//
//		/* 2. データの作成 */
//
//		// パラメータ
//		StringBuffer parameter = new StringBuffer();
//		parameter.append("oauth_consumer_key=");
//		parameter.append(oauth_consumer_key);
//		parameter.append("&oauth_nonce=");
//		parameter.append(oauth_nonce);
//		parameter.append("&oauth_signature_method=");
//		parameter.append(oauth_signature_method);
//		parameter.append("&oauth_timestamp=");
//		parameter.append(timestamp);
//		parameter.append("&oauth_token=");
//		parameter.append(oauth_token);
//		parameter.append("&oauth_version=");
//		parameter.append(oauth_version);
//		parameter.append("&");
//		parameter.append(requestQuery);// has Key and Value
//
//		// メソッド、リクエストURL、パラメータを&で繋げる
//		StringBuffer data = new StringBuffer();
//		try {
//			data.append(URLEncoder.encode(requestMethod, "UTF-8"));
//			data.append("&");
//			data.append(URLEncoder.encode(requestURL, "UTF-8"));
//			data.append("&");
//			data.append(URLEncoder.encode(parameter.toString(), "UTF-8"));
//		} catch (Exception e) {
//		}
//		String signature = null;
//		try {
//			byte[] signature_sha1 = calculateHmacSHA1(data.toString(), key);
//			String signature_base64 = Base64.getEncoder().encodeToString(signature_sha1);
//			signature = URLEncoder.encode(signature_base64, "UTF-8");
//		} catch (Exception e) {
//
//		}
//
//		return signature;
//
//	}

//	private static byte[] calculateHmacSHA1(String data, String key)
//			throws java.security.SignatureException {
//
//		byte[] rawHmac = null;
//		try {
//			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(),
//					"HmacSHA1");
//			Mac mac = Mac.getInstance("HmacSHA1");
//			mac.init(secretKey);
//			rawHmac = mac.doFinal(data.getBytes());
//		} catch (Exception e) {
//			// throw new SignatureException("Failed to generate HMAC : " +
//			// e.getMessage());
//		}
//		return rawHmac;
//	}
}