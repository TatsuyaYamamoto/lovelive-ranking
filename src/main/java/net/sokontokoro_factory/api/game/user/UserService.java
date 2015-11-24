package net.sokontokoro_factory.api.game.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.util.Property;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONException;
import org.json.JSONObject;


class UserService{
	


	private static Connection getConnection() {

        Connection connection = null;
		try {
	        Class.forName(Property.DB_DRIVER());
	        connection = DriverManager.getConnection(Property.DB_URL(), Property.DB_USER(), Property.DB_PASSWORD());
			return connection;
		} catch (ConfigurationException e){
		} catch (ClassNotFoundException e){
		} catch (SQLException e){
		}
		return connection;
	}

	protected static JSONObject getUserName(int user_id) throws SQLException{
	
		String sql = "select * from user where id = ?";
							
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONObject userInfo = new JSONObject();
		try{
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			rs = statement.executeQuery();
			if(rs.next()){
				userInfo.put("user_id", rs.getInt("id"));
				userInfo.put("user_name", rs.getString("name"));				
			}
		}catch (JSONException e) {
			throw e;
		}catch (SQLException e) {
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
		return userInfo;
	}
	
	protected static void registration(
								int user_id,
								String user_name)
								throws Exception {
		
		String sql = "insert into user (id, name, create_date, update_date)"
						+ " values (?, ?, now(), now())"
						+ " ON DUPLICATE KEY UPDATE"
						+ " name = values(name), update_date = now()";
									
		PreparedStatement statement = null;
		Connection connection = getConnection();

		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.setString(2, user_name);
			statement.executeUpdate();
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
