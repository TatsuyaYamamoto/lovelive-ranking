package net.sokontokoro_factory.api.game.service;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sokontokoro_factory.api.game.dto.ScoreDto;
import net.sokontokoro_factory.api.game.dto.UserDto;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;





public class ScoreServiceTest {

	@Test
	public void ユーザーとゲームを指定して情報を取得できること() throws ClassNotFoundException, SQLException{
		int executeUserId = 18130926;
		String executeGameName = "game1";
		String expectGameName = executeGameName;
		int expectPoint = 10;
		int expectRanking = 4;
		
		ScoreDto score = ScoreService.getScore(executeUserId, executeGameName);
		
		/* assert */
		assertThat(score.getGameName(), is(expectGameName));
		assertThat(score.getPoint(), is(expectPoint));
		assertThat(score.getRanking(), is(expectRanking));
	}
	
	@Test
	public void ユーザーを指定してすべてのゲームの点数情報を取得できること() throws ClassNotFoundException, SQLException {
		ArrayList<ScoreDto> scores = ScoreService.getScores(18130926);
		assertThat(scores.size(), is(2));
	}
	@Before
	public void setUpBeforeClass() throws Exception {

		IDatabaseConnection iDatabaseConnection = null;
		
		try(
			//Connectionの取得
			Connection connection = getConnectionForBeforeAndAfter();){
			//IDatabaseConnectionの作成
			iDatabaseConnection = new DatabaseConnection(connection);
			//データセットの取得
			IDataSet dataset = new CsvDataSet(new File("./src/test/resources/dbunit"));
			//セットアップ実行
			DatabaseOperation.CLEAN_INSERT.execute(iDatabaseConnection, dataset);
		}finally{
			if(iDatabaseConnection != null){
				iDatabaseConnection.close();
			}
		}
	}
	
	private Connection getConnectionForBeforeAndAfter() throws SQLException{
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/sokontokoro_game",
				"root","");
		return conn;
	}
	
	@Test
	public void  mysqlコネクションを取得できること(){
		Connection connection = null;
		try{
			ScoreService scoreService = new ScoreService();
			Method method = ScoreService.class.getDeclaredMethod("getConnection");
			method.setAccessible(true);
			connection = (Connection)method.invoke(scoreService);			
		}catch(Exception e){
			e.printStackTrace();
		}
		assertNotNull(connection);
		
	}
}
