package net.sokontokoro_factory.api.game.ranking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.game.util.Property;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

public class RankingService {

	
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
	
	public static String getMine(String game_name) {
		// TODO Auto-generated method stub
		return null;
	}

	public static JSONArray getHigher(
							String game_name, 
							int NUMBER_OF_TOP)
							throws Exception{

		String sql = "select * from game_score where game_name = ? ORDER BY 'point' limit ?";

		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		JSONObject score = new JSONObject();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setInt(2,  NUMBER_OF_TOP);
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






