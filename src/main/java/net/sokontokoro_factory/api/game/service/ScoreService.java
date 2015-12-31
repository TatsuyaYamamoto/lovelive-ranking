package net.sokontokoro_factory.api.game.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sokontokoro_factory.api.game.dto.ScoreDto;
import net.sokontokoro_factory.api.util.Config;


public class ScoreService {

	private static final int NUMBER_OF_TOP = Config.getInt("ranking.top.number");
	
	/**
	 * DBconnectionを返す
	 * @return Connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getConnection() throws ClassNotFoundException, SQLException{
        Connection connection = null;
		try {
	        Class.forName(Config.getString("db.driver"));
	        connection = DriverManager
	        				.getConnection(
	        					Config.getString("db.url"), 
	        					Config.getString("db.user"), 
	        					Config.getString("db.password"));
		} catch (ClassNotFoundException| SQLException e){
			e.printStackTrace();
			throw e;
		}
		return connection;
	}
	
	/**
	 * userId, game_name, pointからスコア情報を登録する
	 * @param game_name
	 * @param user_id
	 * @param point
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void insertScore(
							String game_name, 
							int user_id, 
							int point)
							throws SQLException, ClassNotFoundException{

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

	/**
	 * 指定ユーザーのスコア情報(得点、順位)を返す
	 * @param userId
	 * @return {user_id: ***, game_name: ***, point: ***}
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<ScoreDto> getScores(int userId)throws SQLException, ClassNotFoundException {

		String sql = "select "
				+ "*, "
				+ "(select count(*)+1 as ranking  from score "
					+ "where point > (select point from score where user_id = ? and game_name = target.game_name) "
					+ "and game_name = target.game_name) "
					+ "as ranking "
				+ "from score target "
				+ "where user_id = ?";
		
		ArrayList<ScoreDto> scores = new ArrayList<ScoreDto>();
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setInt(1, userId);
			statement.setInt(2, userId);
			try(ResultSet rs = statement.executeQuery();){
				while (rs.next()) {
					ScoreDto score = new ScoreDto();
					score.setGameName(rs.getString("game_name"));
					score.setPoint(rs.getInt("point"));
					score.setCreateDate(rs.getTimestamp("create_date"));
					score.setUpdateDate(rs.getTimestamp("update_date"));
					score.setFinalDate(rs.getTimestamp("final_date"));
					score.setCount(rs.getInt("count"));
					score.setRanking(rs.getInt("ranking"));
					
					scores.add(score);
				}
			}
		}catch(ClassNotFoundException | SQLException e){
			e.printStackTrace();
			throw e;
		}
		return scores;
	}
	
	/**
	 * 指定ユーザー、ゲームの得点を返す
	 * @param user_id
	 * @param gameName
	 * @return	ユーザーが得点を登録済み：scoreDto
	 * 			ユーザーが得点を未登録：null
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static ScoreDto getScore(int userId, String gameName)throws SQLException, ClassNotFoundException {

		String sql = 
				"select *, (select count(*)+1 as ranking"
					+ " from score"
					+ " where point > (select point from score where user_id = ? and game_name = target.game_name)"
					+ " and game_name = target.game_name)as ranking"
				+ " from score target"
				+ " where user_id = ?"
				+ " and game_name = ?";
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setInt(1, userId);
			statement.setInt(2, userId);
			statement.setString(3, gameName);
			
			ScoreDto score = null;
			
			try(ResultSet rs = statement.executeQuery();){
				while(rs.next()){
					score = new ScoreDto();
					score.setGameName(rs.getString("game_name"));
					score.setPoint(rs.getInt("point"));
					score.setUpdateDate(rs.getTimestamp("update_date"));
					score.setCount(rs.getInt("count"));
					score.setRanking(rs.getInt("ranking"));
				}
			}
			
			return score;
		}catch(ClassNotFoundException | SQLException e){
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 指定したゲームのスコア登録の総人数を返す
	 * @param game_name
	 * @return {game_name: ***, count: ***}
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static int getTotalNumber(String game_name)throws SQLException, ClassNotFoundException {

		String sql = "select count(*) from score where game_name = ?";
		int total;
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setString(1, game_name);
			try(ResultSet rs = statement.executeQuery();){
				rs.next();
				total = rs.getInt("count(*)");
			}	
		}catch(SQLException | ClassNotFoundException e){
			e.printStackTrace();
			throw e;
		}
		return total;
	}
	
	/**
	 * 引数で指定した順位までのランキング情報を返す
	 * [{game_name: ***, user_name: ***, point: ***}]
	 * @param game_name
	 * @param NUMBER_OF_TOP
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<ScoreDto> getHigher(String gameName) throws SQLException, ClassNotFoundException{

		String subSql = "(select count(*) from score as targetScore where game_name= ? and targetScore.point > score.point)";
		/**
		 * サブクエリ: 同値考慮のカウント
		 * targetのpointより大きいpointの件数をcountする
		 * 注意：最小値は0
		 */
		
		String sql = "select score.user_id, user.name as user_name, score.point, " + subSql + " + 1 as ranking"
				+ " from score left join user on score.user_id = user.id"
				+ " where game_name = ?"
				+ " and point in (select distinct point from (select distinct point from score where game_name = ? ORDER BY point DESC) as table1)"
				+ " and " + subSql +" < ?"
				+ " order by point desc";
		/**
		 * 1. user_id, user_name, point, rankingカウント(最小値0考慮)を表示する
		 * 2. userIdをkeyにscore tableとuser tableをleft joinする
		 * 3. 条件１：ゲーム
		 * 4. 条件２；
		 * 5. 条件３：同値考慮のランキングを?まで表示する(最小値が0なので、?の値を含む条件になる)
		 * 6. 降順で表示する
		 */
		
		
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){

			ArrayList<ScoreDto> scores = new ArrayList<ScoreDto>();
			
			statement.setString(1, gameName);
			statement.setString(2, gameName);
			statement.setString(3, gameName);
			statement.setString(4, gameName);
			statement.setInt(5, NUMBER_OF_TOP);
			try(ResultSet rs = statement.executeQuery();){

				while (rs.next()) {
					ScoreDto score = new ScoreDto();
					score.setUserName(rs.getString("user_name"));
					score.setUserId(rs.getInt("user_id"));
					score.setPoint(rs.getInt("point"));
					score.setRanking(rs.getInt("ranking"));
					scores.add(score);
				}		
			}
			return scores;

		}catch(SQLException | ClassNotFoundException e){
			e.printStackTrace();
			throw e;	
		}
	}
}

