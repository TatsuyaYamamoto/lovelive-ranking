package net.sokontokoro_factory.api.util;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Property {

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

	// システム関連--------------------------------------------
	public static String GAME_CLIENT_ORIGIN()
			throws ConfigurationException {
		return getConfiguration().getString("game.client.origin");
	}
	public static String SERVER_ORIGIN()
			throws ConfigurationException {
		return getConfiguration().getString("server.origin");
	}
	
	public static int RANKING_TOP_NUMBER()
			throws ConfigurationException {
		return getConfiguration().getInt("ranking.top.number");
	}

	// MySQL関連--------------------------------------------
	public static String DB_DRIVER()
			throws ConfigurationException {
		return getConfiguration().getString("db.driver");
	}
	public static String DB_URL()
			throws ConfigurationException {
		return getConfiguration().getString("db.url");
	}
	public static String DB_USER()
			throws ConfigurationException {
		return getConfiguration().getString("db.user");
	}
	public static String DB_PASSWORD()
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

	public static String ACCESS_TOKEN_URL() throws ConfigurationException{
		return getConfiguration().getString("access.token.URL");
	}
	public static String REQUEST_TOKEN_URL() throws ConfigurationException{
		return getConfiguration().getString("request.token.URL");
	}
	public static String AUTHENTICATE_URL() throws ConfigurationException{
		return getConfiguration().getString("authenticate.URLWithKey");
	}
}