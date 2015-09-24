package net.sokontokoro_factory.api.game.score;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.game.util.Property;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScoreService {


	private static Connection getConnection() {

        Connection connection = null;
		try {
	        Class.forName(Property.DBDriver());
	        connection = DriverManager.getConnection(Property.DBUrl(), Property.DBUser(), Property.DBPassword());
			return connection;
		} catch (ConfigurationException e){
		} catch (ClassNotFoundException e){
		} catch (SQLException e){
		}
		return connection;
	}
	
	
	public static void insertScore(
								String game_name, 
								String category,
								int user_id, 
								int point)
								throws SQLException {

		String sql = "INSERT INTO game_score"
				+ " (game_name, category, user_id, point, create_date,update_date,final_date,count)"
				+ " VALUES (?,?,?,?,NOW(),NOW(),NOW(),1)"	// 初回登録
				+ " ON DUPLICATE KEY UPDATE"				// ↓2回目以降
				+ " point = IF(VALUES(point) > point, VALUES(point), point),"
				+ " update_date = IF(VALUES(point) > point, NOW(), update_date),"
				+ " final_date = NOW(),"
				+ " count = count + 1";
				
		PreparedStatement statement = null;
		Connection connection = getConnection();

		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setString(2, category);
			statement.setInt(3, user_id);
			statement.setInt(4, point);
			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			try{
				if (connection != null) {
					connection.close();
				}
				if (statement != null) {
					statement.close();
				}
			}catch(SQLException e){
				throw e;
			}
		}
	}

	public static JSONArray getScores(
								String game_name)
								throws Exception{

		String sql = "select * from game_score where game_name = ?";

		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		JSONObject score = new JSONObject();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			rs = statement.executeQuery();
			while (rs.next()) {
				score.put("game_name", rs.getString("game_name"));
				score.put("category", rs.getString("category"));
				score.put("user_id", rs.getInt("user_id"));
				score.put("point", rs.getInt("point"));
				scores.put(score);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return scores;
	}
	public static JSONArray getMyScore(
			String game_name, 
			int user_id)
			throws Exception{

		String sql = "select * from game_score where game_name = ? and user_id = ?";

		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		JSONObject score = new JSONObject();

		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setInt(2, user_id);
			rs = statement.executeQuery();
			while (rs.next()) {
				score.put("game_name", rs.getString("game_name"));
				score.put("category", rs.getString("category"));
				score.put("user_id", rs.getInt("user_id"));
				score.put("point", rs.getInt("point"));
				score.put("create_date", rs.getTimestamp("create_date"));
				score.put("update_date", rs.getTimestamp("update_date"));
				score.put("final_date", rs.getTimestamp("final_date"));
				score.put("count", rs.getInt("count"));
				scores.put(score);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return scores;
	}
	public static JSONArray getHigher(
			String game_name, 
			int NUMBER_OF_TOP)
			throws Exception{
		String sql = "select * from game_score where game_name = ? limit ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		JSONObject score = new JSONObject();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setInt(2, NUMBER_OF_TOP);
			rs = statement.executeQuery();
			while (rs.next()) {
				score.put("game_name", rs.getString("game_name"));
				score.put("category", rs.getString("category"));
				score.put("user_id", rs.getInt("user_id"));
				score.put("point", rs.getInt("point"));
				scores.put(score);
			}
			return scores;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}

