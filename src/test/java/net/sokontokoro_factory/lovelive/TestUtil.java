package net.sokontokoro_factory.lovelive;

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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static final String DATABASE_URL         = "jdbc:mysql://localhost:3306/lovelive_game_db";
    public static final String DATABASE_USER        = "root";
    public static final String DATABASE_PASSWORD    = "";
    public static final String DBUNIT_RESOURCE_DIR  = "dbUnit/";

    /**
     * privateフィールド変数に対するセッター
     *
     * @param targetObject
     * @param fieldName
     * @param value
     * @throws Exception
     */
    public static void setPrivateField(Object targetObject, String fieldName, Object value) throws Exception{
        Class clazz = targetObject.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);

    }
    public static void setPrivateField(Class targetClass, String fieldName, Object value) throws Exception{
        Field field = targetClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetClass, value);

    }

    /**
     * privateフィールド変数に対するゲッター
     *
     * @param targetObject
     * @param fieldName
     * @return
     * @throws Exception
     */
    public static Object getPrivateField(Object targetObject, String fieldName) throws Exception{
        Class clazz = targetObject.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(targetObject);
    }
    public static Object getPrivateField(Class targetClass, String fieldName) throws Exception{
        Field field = targetClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(targetClass);
    }

    /**
     * クラス内に定義されたインスタンスのフィールド変数に方に合わせて一時的な値を代入する。
     * setter, getterカバレッジ用
     *
     * @param targetObject
     * @return
     * @throws IllegalAccessException
     */
    public static List<Method> getAllGetter(Object targetObject) throws IllegalAccessException, IntrospectionException {
        List<Method> getterMethods = new ArrayList();

        Field[] allFields = targetObject.getClass().getDeclaredFields();
        for (Field field : allFields) {

            // jacocoによるカバレッジリポート用のfieldが挿入されているので、無視する
            if(!field.getName().equals("$jacocoData")){
                // getterを取得
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), targetObject.getClass());
                getterMethods.add(pd.getReadMethod());
            }
        }
        return getterMethods;
    }

    public static List<Field> getAllField(Object targetObject) throws IllegalAccessException, IntrospectionException {
        List<Field> fields = new ArrayList();

        Field[] allFields = targetObject.getClass().getDeclaredFields();
        for (Field field : allFields) {

            // jacocoによるカバレッジリポート用のfieldが挿入されているので、無視する
            if(
                    !field.getName().equals("$jacocoData") &&
                            !field.getName().equals("serialVersionUID")){
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * CSVファイルをレコードオブジェクトのリストに変換する。
     * 読み込み対象のディレクトリは/resources/dbUnit
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<CSVRecord> loadDBUnitCSVFile(String fileName) throws IOException {

        String filePath = TestUtil.class.getClassLoader().getResource("dbUnit/" + fileName).getPath();
        File csvFile = new File(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));

        CSVParser parse = CSVFormat.EXCEL
                .withIgnoreEmptyLines(true)         // 空白行を無視する
                .withIgnoreSurroundingSpaces(true)  // ""、及び""の外側のスペースを削除する
                .withCommentMarker('#')             // #で始まる行をコメントとして扱う
                .parse(br);

        return parse.getRecords();


    }

    /**
     * データーベースのバックアップファイルを作成する。
     * バックアップファイルはシステムが指定するtmpディレクトリ
     *
     * @param targetTables
     * @return
     * @throws Exception
     */
    public static File createDatabaseBackupFile(String[] targetTables) throws Exception{
        Connection connection = null;
        IDatabaseConnection databaseConnection = null;
        File backupFile = null;
        try{
            connection = getConnection();
            databaseConnection = new DatabaseConnection(connection);

            // backup
            QueryDataSet backupDataset = new QueryDataSet(databaseConnection);
            for(String targetTable: targetTables){
                backupDataset.addTable(targetTable);
            }

            backupFile = File.createTempFile("testdb_bak", ".xml");

            XmlDataSet.write(backupDataset, new FileOutputStream(backupFile));


        }finally {
            if(databaseConnection != null){
                databaseConnection.close();
            }
            if(connection != null){
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
    public static void importTestDataSet() throws SQLException, DatabaseUnitException, ClassNotFoundException{
        Connection connection = null;
        IDatabaseConnection databaseConnection = null;
        try{
            connection = getConnection();
            databaseConnection = new DatabaseConnection(connection);

            // CLEAN_INSERT用に外部キーチェックを外す
            connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0")
                    .executeQuery();

            // read and insert
            URL url = TestUtil.class.getClassLoader().getResource(TestUtil.DBUNIT_RESOURCE_DIR);
            CsvURLDataSet dataSet = new CsvURLDataSet(url);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);

            // 外部キーチェック再開
            connection
                    .prepareStatement("SET FOREIGN_KEY_CHECKS=1")
                    .executeQuery();
        }finally {
            if(databaseConnection != null){
                databaseConnection.close();
            }
            if(connection != null){
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
    public static void importBackupFileToDatabase(File backupFile)
            throws SQLException, IOException, DatabaseUnitException, ClassNotFoundException{

        Connection connection = null;
        IDatabaseConnection databaseConnection = null;
        try{
            connection = getConnection();
            databaseConnection = new DatabaseConnection(connection);

            // CLEAN_INSERT用に外部キーチェックを外す
            connection
                    .prepareStatement("SET FOREIGN_KEY_CHECKS=0")
                    .executeQuery();

            // rollback
            XmlDataSet dataSet = new XmlDataSet(new FileInputStream(backupFile));
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);

            // 外部キーチェック再開
            connection
                    .prepareStatement("SET FOREIGN_KEY_CHECKS=1")
                    .executeQuery();
        }finally {
            if(databaseConnection != null){
                databaseConnection.close();
            }
            if(connection != null){
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
        Connection connection = DriverManager.getConnection(
                TestUtil.DATABASE_URL,
                TestUtil.DATABASE_USER,
                TestUtil.DATABASE_PASSWORD);
        return connection;
    }
}