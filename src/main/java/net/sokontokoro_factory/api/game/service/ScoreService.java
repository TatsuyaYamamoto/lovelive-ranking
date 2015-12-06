package net.sokontokoro_factory.api.game.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.util.Config;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScoreService {


	private static Connection getConnection() throws ClassNotFoundException, SQLException{

        Connection connection = null;
		try {
	        Class.forName(Config.getString("db.driver"));
	        connection = DriverManager.getConnection(Config.getString("db.url"), Config.getString("db.user"), Config.getString("db.password"));
		} catch (ClassNotFoundException| SQLException e){
			e.printStackTrace();
			throw e;
		}
		return connection;
	}
	
	
	public static void insertScore(
								String game_name, 
								int user_id, 
								int point)throws SQLException, ClassNotFoundException{

		String sql = "INSERT INTO score"
				+ " (game_name, user_id, point, create_date,update_date,final_date,count)"
				+ " VALUES (?,?,?,NOW(),NOW(),NOW(),1)"	// 初回登録
				+ " ON DUPLICATE KEY UPDATE"				// ↓2回目以降
				+ " update_date = IF(VALUES(point) > point, values(update_date), update_date),"
				+ " point = IF(VALUES(point) > point, VALUES(point), point),"
				+ " final_date = NOW(),"
				+ " count = count + 1";
		
		try(Connection connection = getConnection();
			PreparedStatement statement	= connection.prepareStatement(sql);){
			statement.setString(1, game_name);
			statement.setInt(2, user_id);
			statement.setInt(3, point);
			statement.executeUpdate();
		}catch(SQLException | ClassNotFoundException e){
			e.printStackTrace();
			throw e;
		}
	}

	public static JSONArray getScores(String game_name)throws SQLException, ClassNotFoundException {

		String sql = "select * from score where game_name = ?";
		JSONArray scores = new JSONArray();
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setString(1, game_name);
			try(ResultSet rs = statement.executeQuery();){
				while (rs.next()) {
					JSONObject score = new JSONObject();
					score.put("point", rs.getInt("point"));
					scores.put(score);
				}
			}
		}catch(ClassNotFoundException | SQLException e){
			e.printStackTrace();
			throw e;
		}
		return scores;
	}
	public static JSONObject getTotalNumber(String game_name)throws SQLException, ClassNotFoundException {

		String sql = "select count(*) from score where game_name = ?";
		JSONObject result = new JSONObject();
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setString(1, game_name);
			try(ResultSet rs = statement.executeQuery();){
				rs.next();
				result.put("count", rs.getInt("count(*)"));				
			}	
		}catch(SQLException | ClassNotFoundException e){
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	public static JSONObject getMyInfo(
			String game_name, 
			int user_id)
			throws Exception{

		String sql_getRanking = "select count(*)+1 as ranking  from score"
				+ " where point > (select point from score where user_id = ? and game_name = ?)"
				+ " and game_name = ?";
		
		
		String sql_userInfo = " select * , user.name as user_name from score"
				+ " left join user"
				+ " on score.user_id = user.id"
				+ " where user_id = ?"
				+ " and game_name = ?";
		JSONObject info = new JSONObject();
		
		try(Connection connection = getConnection();
			PreparedStatement statement_getRanking = connection.prepareStatement(sql_getRanking);
			PreparedStatement statement_userInfo = connection.prepareStatement(sql_userInfo);){
			connection.setAutoCommit(false);
			
			statement_getRanking.setInt(1, user_id);
			statement_getRanking.setString(2, game_name);
			statement_getRanking.setString(3, game_name);
			try(ResultSet rs_getRanking = statement_getRanking.executeQuery();
				ResultSet rs_userInfo = statement_userInfo.executeQuery();){
				
				rs_getRanking.next();
				info.put("ranking", rs_getRanking.getInt("ranking"));
				
				rs_userInfo.next();			
				info.put("game_name", rs_userInfo.getString("game_name"));
				info.put("user_name", rs_userInfo.getString("user_name"));
				info.put("user_id", rs_userInfo.getInt("user_id"));
				info.put("point", rs_userInfo.getInt("point"));
				// 2015.10の仕様では提供する必要がない情報
//				info.put("create_date", rs.getTimestamp("create_date"));
//				info.put("update_date", rs.getTimestamp("update_date"));
//				info.put("final_date", rs.getTimestamp("final_date"));
//				info.put("count", rs.getInt("count"));
			
			}catch(SQLException e){
				connection.rollback();
				e.printStackTrace();
				throw e;	
			}
			connection.commit();
		}
		return info;
	}
	public static JSONArray getHigher(
			String game_name, 
			int NUMBER_OF_TOP)
			throws Exception{
		String sql = "select *, user.name as user_name from score"
				+ " left join user"
				+ " on score.user_id = user.id"
				+ " where game_name = ?"
				+ " ORDER BY point DESC limit ?";
		JSONArray scores = new JSONArray();
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setString(1, game_name);
			statement.setInt(2, NUMBER_OF_TOP);
			try(ResultSet rs = statement.executeQuery();){
				while (rs.next()) {
					JSONObject score = new JSONObject();
					score.put("game_name", rs.getString("game_name"));
					score.put("user_name", rs.getString("user_name"));
					score.put("point", rs.getInt("point"));
					scores.put(score);
				}		
			}
		}
		return scores;
	}
}

