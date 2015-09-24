package net.sokontokoro_factory.api.twitter.util;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Properties {

	private static PropertiesConfiguration config;

	private static PropertiesConfiguration getConfiguration()
			throws ConfigurationException {
		if (config == null) {
			config = new PropertiesConfiguration("config.properties");
		}
		return config;
	}

	// メッセージ関連--------------------------------------------
	public static String testText()
			throws ConfigurationException {
		return getConfiguration().getString("test.text");
	}


	// MySQL関連--------------------------------------------
	public static String DBDriver()
			throws ConfigurationException {
		return getConfiguration().getString("db.driver");
	}
	public static String DBUrl()
			throws ConfigurationException {
		return getConfiguration().getString("db.url");
	}
	public static String DBUser()
			throws ConfigurationException {
		return getConfiguration().getString("db.user");
	}
	public static String DBPassword()
			throws ConfigurationException {
		return getConfiguration().getString("db.password");
	}
	
	
	// Twtter OAuth関連--------------------------------------------
	public static String CONSUMER_KEY() throws ConfigurationException{
		return getConfiguration().getString("consumerKey");
	}
	public static String CONSUMER_SECRET() throws ConfigurationException{
		return getConfiguration().getString("consumerSecret");
	}

	// signature--------------------------------------------
	public static String SIGNATURE_METHOD() throws ConfigurationException{
		return getConfiguration().getString("signature.method");
	}
	public static String SIGNATURE_VERSION() throws ConfigurationException{
		return getConfiguration().getString("signature.version");
	}

	// origin, endpoint--------------------------------------------
	public static String ORIGIN_PORTAL() throws ConfigurationException{
		return getConfiguration().getString("origin.portal");
	}
	public static String ENDPOINT_APP_REDIRECT() throws ConfigurationException{
		return getConfiguration().getString("endpoint.app.redirect");
	}
	public static String ENDPOINT_OAUTH_TOKEN() throws ConfigurationException{
		return getConfiguration().getString("endpoint.oauth.token");
	}
	public static String QUERYKEY_OAUTH_TOKEN() throws ConfigurationException{
		return getConfiguration().getString("queryKey.oauth.token");
	}
	public static String ENDPOINT_ACCESS_TOKEN() throws ConfigurationException{
		return getConfiguration().getString("endpoint.access.token");
	}
	public static String ENDPOINT_REQUEST_TOKEN() throws ConfigurationException{
		return getConfiguration().getString("endpoint.request.token");
	}
}