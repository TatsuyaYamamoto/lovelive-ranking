package net.sokontokoro_factory.lovelive;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;

public class TestDatabase {
  public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/lovelive_game_db";
  public static final String DATABASE_USER = "root";
  public static final String DATABASE_PASSWORD = "";
  public static final String DBUNIT_RESOURCE_DIR = "dbUnit/";

  private static final String[] BACKUP_TARGET_TABLES = {"USER", "SCORE", "GAME_LOG"};

  /**
   * CSVファイルをレコードオブジェクトのリストに変換する。 読み込み対象のディレクトリは/resources/dbUnit
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static List<CSVRecord> loadDBUnitCSVFile(String fileName) throws IOException {

    String filePath =
        TestDatabase.class.getClassLoader().getResource("dbUnit/" + fileName).getPath();
    File csvFile = new File(filePath);
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));

    CSVParser parse =
        CSVFormat.EXCEL
            .withIgnoreEmptyLines(true) // 空白行を無視する
            .withIgnoreSurroundingSpaces(true) // ""、及び""の外側のスペースを削除する
            .withCommentMarker('#') // #で始まる行をコメントとして扱う
            .parse(br);

    return parse.getRecords();
  }

  /**
   * データーベースのバックアップファイルを作成する。 バックアップファイルはシステムが指定するtmpディレクトリ
   *
   * @return
   * @throws Exception
   */
  public static File createBackupFile() throws Exception {
    Connection connection = null;
    IDatabaseConnection databaseConnection = null;
    File backupFile = null;
    try {
      connection = getConnection();
      databaseConnection = new DatabaseConnection(connection);

      // backup
      QueryDataSet backupDataset = new QueryDataSet(databaseConnection);
      for (String targetTable : BACKUP_TARGET_TABLES) {
        backupDataset.addTable(targetTable);
      }

      backupFile = File.createTempFile("testdb_bak", ".xml");

      XmlDataSet.write(backupDataset, new FileOutputStream(backupFile));

    } finally {
      if (databaseConnection != null) {
        databaseConnection.close();
      }
      if (connection != null) {
        connection.close();
      }
    }
    System.out.println(backupFile.getPath());
    return backupFile;
  }

  /**
   * DBUnitによるDataSetのインポートを実行する
   *
   * @throws Exception
   */
  public static void importTestDataSet()
      throws SQLException, DatabaseUnitException, ClassNotFoundException {
    Connection connection = null;
    IDatabaseConnection databaseConnection = null;
    try {
      connection = getConnection();
      databaseConnection = new DatabaseConnection(connection);

      // CLEAN_INSERT用に外部キーチェックを外す
      connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0").executeQuery();

      // read and insert
      URL url = TestDatabase.class.getClassLoader().getResource(TestDatabase.DBUNIT_RESOURCE_DIR);
      CsvURLDataSet dataSet = new CsvURLDataSet(url);
      DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);

      // 外部キーチェック再開
      connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1").executeQuery();
    } finally {
      if (databaseConnection != null) {
        databaseConnection.close();
      }
      if (connection != null) {
        connection.close();
      }
    }
  }

  /**
   * バックアップしたファイルをDatabaseにインポートする
   *
   * @param backupFile
   * @throws Exception
   */
  public static void importBackupFile(File backupFile)
      throws SQLException, IOException, DatabaseUnitException, ClassNotFoundException {

    Connection connection = null;
    IDatabaseConnection databaseConnection = null;
    try {
      connection = getConnection();
      databaseConnection = new DatabaseConnection(connection);

      // CLEAN_INSERT用に外部キーチェックを外す
      connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0").executeQuery();

      // rollback
      XmlDataSet dataSet = new XmlDataSet(new FileInputStream(backupFile));
      DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);

      // 外部キーチェック再開
      connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1").executeQuery();
    } finally {
      if (databaseConnection != null) {
        databaseConnection.close();
      }
      if (connection != null) {
        connection.close();
      }
      backupFile.delete();
    }
  }

  /**
   * JDBCのコネクションを取得する。
   *
   * @return
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private static Connection getConnection() throws SQLException, ClassNotFoundException {
    Connection connection =
        DriverManager.getConnection(
            TestDatabase.DATABASE_URL, TestDatabase.DATABASE_USER, TestDatabase.DATABASE_PASSWORD);
    return connection;
  }
}
