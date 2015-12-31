package net.sokontokoro_factory.api.game.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.game.dto.UserDto;
import net.sokontokoro_factory.api.util.Config;

import org.apache.commons.configuration.ConfigurationException;

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
	 * 登録されているユーザーアカウントのステータスを確認する
	 * 登録済み：true
	 * 削除済み or 未登録：false
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static boolean isValidId(int userId){
		String sql = "select * from user where id = ?";
		boolean validation = false;
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setInt(1, userId);
			try(ResultSet rs = statement.executeQuery();){
				while(rs.next()){
					if(!rs.getBoolean("deleted")){
						validation = true;
					}
				}
			}
		}catch(ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return validation;
	}
	
	/**
	 * 指定したuser_idに対するuser情報を取得する。
	 * @param UserDto idのみ参照
	 * @return UserDto
	 * @throws SQLException
	 */
	public static UserDto getDetail(UserDto user) throws SQLException{
	
		String sql = "select * from user where id = ? and deleted != true";
		UserDto getUser = new UserDto();
		
		try(Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);){
			statement.setInt(1, user.getId());
			try(ResultSet rs = statement.executeQuery();){
				while(rs.next()){
					getUser.setId(rs.getInt("id"));
					getUser.setName(rs.getString("name"));
					getUser.setCreateDate(rs.getTimestamp("create_date"));
					getUser.setUpdateDate(rs.getTimestamp("update_date"));					
				}
			}
		}catch(ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return getUser;
	}
	
	/**
	 * ユーザーを登録する
	 * user_idが重複している場合、name, updateDate, deletedの値が再入力される
	 * 重複の場合とは、登録済みユーザーによるname更新か、削除済みユーザーのアカウント有効化のとき
	 * @param 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException 
	 */
	public static UserDto registration(UserDto user)throws ClassNotFoundException, SQLException{
		String sql = "insert into user (id, name, create_date, update_date)"
				+ " values (?, ?, now(), now())"
				+ " ON DUPLICATE KEY UPDATE"
				+ " name = values(name), update_date = now(), deleted = false";
		try(Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);){
				statement.setInt(1, user.getId());
				statement.setString(2, user.getName());
				statement.executeUpdate();
			}catch(ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				throw e;
			}
			return getDetail(user);
	}

	public static UserDto delete(UserDto deleteUser) throws SQLException, ClassNotFoundException{
		String sql = "update user set deleted = true where id = ?";
		UserDto deletingUser = getDetail(deleteUser);
		try(Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);){
				statement.setInt(1, deleteUser.getId());
				statement.executeUpdate();
			}catch(ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				throw e;
			}
		return deletingUser;
	}
}
