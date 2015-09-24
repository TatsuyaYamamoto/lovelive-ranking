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
								int user_id, 
								int point)
								throws SQLException {

		String sql = "INSERT INTO game_score"
				+ " (game_name, user_id, point, create_date,update_date,final_date,count)"
				+ " VALUES (?,?,?,NOW(),NOW(),NOW(),1)"	// 初回登録
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
			statement.setInt(2, user_id);
			statement.setInt(3, point);
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
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			rs = statement.executeQuery();
			while (rs.next()) {
				JSONObject score = new JSONObject();
				score.put("game_name", rs.getString("game_name"));
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
	public static JSONObject getMyInfo(
			String game_name, 
			int user_id)
			throws Exception{

		String sql = "select *, count(*) + 1 as ranking from game_score"
				+ " where point > (select point from game_score where user_id = ?)"
				+ " and game_name = ?";
		
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONObject info = new JSONObject();

		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.setString(2, game_name);
			rs = statement.executeQuery();
			rs.next();
			info.put("game_name", rs.getString("game_name"));
			info.put("category", rs.getString("category"));
			info.put("user_id", rs.getInt("user_id"));
			info.put("point", rs.getInt("point"));
			info.put("create_date", rs.getTimestamp("create_date"));
			info.put("update_date", rs.getTimestamp("update_date"));
			info.put("final_date", rs.getTimestamp("final_date"));
			info.put("count", rs.getInt("count"));
			info.put("ranking", rs.getInt("ranking"));

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
		return info;
	}
	public static JSONArray getHigher(
			String game_name, 
			int NUMBER_OF_TOP)
			throws Exception{
		String sql = "select * from game_score where game_name = ? ORDER BY point DESC limit ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setInt(2, NUMBER_OF_TOP);
			rs = statement.executeQuery();
			while (rs.next()) {
				JSONObject score = new JSONObject();
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

