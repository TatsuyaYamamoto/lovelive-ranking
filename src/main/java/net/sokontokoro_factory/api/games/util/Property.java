package net.sokontokoro_factory.api.games.util;


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


	// MySQL connection関連--------------------------------------------
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

	// DB SQL　関連-------------------------------------------
	
	public static String scoreTable()
			throws ConfigurationException {
		return getConfiguration().getString("db.score.table");
	}
}