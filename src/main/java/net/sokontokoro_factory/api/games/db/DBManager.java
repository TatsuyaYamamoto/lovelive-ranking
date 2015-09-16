package net.sokontokoro_factory.api.games.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

import net.sokontokoro_factory.api.games.util.Property;

public class DBManager{

	/**
	 * Connectionオブジェクトを返す
	 * @return
	 * @throws Exception
	 */
	private static Connection getConnection() throws Exception{

        Connection connection = null;
		try {
	        Class.forName(Property.DBDriver());
	        connection = DriverManager.getConnection(Property.DBUrl(), Property.DBUser(), Property.DBPassword());
			return connection;
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * 全スコアを取得する
	 * @param game_name
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getScores(String game_name)throws Exception{
		StringBuffer sql = new StringBuffer("select * from gamescore where game_name = ");
		sql.append("'");
		sql.append(game_name);
		sql.append("'");
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		JSONObject score = new JSONObject();
		try {
			statement = connection.prepareStatement(sql.toString());
			rs = statement.executeQuery();
			while (rs.next()) {
				score.put("user_name", rs.getString("user_name"));
				score.put("point", rs.getString("point"));
				score.put("date", rs.getString("date"));
				scores.put(score);
			}
			return scores;
		} catch (Exception e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
				connection = null;
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
				statement = null;
			}
		}
	}
	
	
	public static void insertScore(String gameName, int score) throws Exception{
		
		// SQL文生成
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		sql.append(Property.scoreTable());
//		sql.append(game_name);
		sql.append("'");
		
		PreparedStatement statement = null;
		Connection connection = getConnection();
	}
	
	
	
	// /*
	//  * スコア登録
	//  * 引数：ゲーム名、ユーザー名
	//  */
	// public static void recordScore(ScoreInfo scoreInfo)throws Exception{
	// 	String sql = "insert into gamescore (game_name, user_name, point, date) values (?, ?, ?, ?)";

	// 	PreparedStatement statement = null;
	// 	Connection connection = getConnection();

	// 	try {
	// 		statement = connection.prepareStatement(sql);
	// 		statement.setString(1, scoreInfo.getGame_name());
	// 		statement.setString(2, scoreInfo.getUser_name());
	// 		statement.setString(3, scoreInfo.getPoint()));
	// 		statement.setString(4, scoreInfo.getDate());
	// 		statement.execute();
	// 	} catch (Exception e) {
	// 		LOGGER.error(e);
	// 		throw e;
	// 	} finally {
	// 		if (connection != null) {
	// 			try {
	// 				connection.close();
	// 			} catch (SQLException e) {
	// 			} // ignore
	// 			connection = null;
	// 		}
	// 		if (statement != null) {
	// 			try {
	// 				statement.close();
	// 			} catch (SQLException e) {
	// 			}
	// 			statement = null;
	// 		}
	// 	}
	// }
}