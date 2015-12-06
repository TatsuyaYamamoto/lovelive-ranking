package net.sokontokoro_factory.api.game.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.util.Config;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONObject;


public class UserService{

	/**
	 * 自動コミットをオフにしたjdbcコネクションオブジェクトを返す
	 * @return
	 * @throws ConfigurationException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
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

	/**
	 * 指定したuser_idに対するuser_nameを取得する。
	 * @param user_id
	 * @return JSONObject {user_id:***, user_name: ***}
	 * @throws SQLException
	 */
	public static JSONObject getUserName(int user_id) throws SQLException{
	
		String sql = "select * from user where id = ?";
		JSONObject userInfo = new JSONObject();
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setInt(1, user_id);
			try(ResultSet rs = statement.executeQuery();){
				if(rs.next()){
					userInfo.put("user_id", rs.getInt("id"));
					userInfo.put("user_name", rs.getString("name"));				
				}
			}catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
		}catch(ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return userInfo;
	}
	
	/**
	 * user_id, user_nameを登録する。user_idが重複していた場合、user_nameを更新する。
	 * @param user_id
	 * @param user_name
	 * @throws Exception
	 */
	public static JSONObject registration(
								int user_id,
								String user_name)
								throws SQLException, ClassNotFoundException {
		
		String sql = "insert into user (id, name, create_date, update_date)"
						+ " values (?, ?, now(), now())"
						+ " ON DUPLICATE KEY UPDATE"
						+ " name = values(name), update_date = now()";
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			connection.setAutoCommit(false);
			statement.setInt(1, user_id);
			statement.setString(2, user_name);
			statement.executeUpdate();
			connection.commit();
		}catch(ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return getUserName(user_id);
	}
}
