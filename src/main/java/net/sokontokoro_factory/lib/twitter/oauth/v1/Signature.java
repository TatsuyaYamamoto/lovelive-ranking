package net.sokontokoro_factory.lib.twitter.oauth.v1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.TreeMap;

import net.sokontokoro_factory.lib.twitter.util.Calculation;
import net.sokontokoro_factory.lib.twitter.util.Config;

public class Signature {
	static String OAUTH_VERSION = "1.0";
	static String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
	
	/**
	 * 
	 * @param consumer_secret
	 * @param oauth_token_secret
	 * @param requestMethod
	 * @param requestURL
	 * @param element
	 * @return
	 */
	public static String generate(
			String consumer_secret,
			String oauth_token_secret,
			String requestMethod,
			String requestURL,
			TreeMap<String,String> element){

		String key = createKey(consumer_secret, oauth_token_secret);
		String data = createData(requestMethod, requestURL, element);

		return calculateSignature(data.toString(), key);
	}
	
	/**
	 * signature用のkeyを作成する
	 * @param consumer_secret
	 * @param oauth_token_secret
	 * @return
	 */
	public static String createKey(String consumer_secret, String oauth_token_secret){
		return consumer_secret + "&" + oauth_token_secret;
	}
	
	/**
	 * signature用のdata(Signature base string)を作成する
	 * @param requestMethod
	 * @param requestURL
	 * @param element
	 * @return
	 */
	public static String createData(
			String requestMethod,
			String requestURL,
			TreeMap<String,String> element){

		TreeMap<String,String> param = new TreeMap<String,String>();
		param.put("oauth_consumer_key", Config.getString("consumer.key"));
		param.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
		param.put("oauth_version", OAUTH_VERSION);
		param.putAll(element);
		
		StringBuffer parameter = new StringBuffer();	
		int i = 0;
		for(String key: element.keySet()){
			if(i!=0) parameter.append("&");
			parameter.append(key);
			parameter.append("=");
			parameter.append(element.get(key));
			i++;
		}
		StringBuffer data = new StringBuffer();
		try {
			data.append(URLEncoder.encode(requestMethod, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(requestURL, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode(parameter.toString(), "UTF-8"));
		} catch (Exception e) {}
		
		return data.toString();
	}
	
	/**
	 * dataとkeyからsignatureを作成する
	 * @param data
	 * @param key
	 * @return
	 */
	public static String calculateSignature(String data, String key){
		byte[] signature_sha1 = null;
		String signature = null;
		try {
			signature_sha1 = Calculation.calcHmacSHA1(data, key);
			String signature_base64 = Base64.getEncoder().encodeToString(signature_sha1);
			signature = URLEncoder.encode(signature_base64, "UTF-8");
		} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return signature;
	}
}
