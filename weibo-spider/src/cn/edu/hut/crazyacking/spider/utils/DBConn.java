package cn.edu.hut.crazyacking.spider.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

public class DBConn {
    private static final Logger Log = Logger.getLogger(DBConn.class.getName());
    public static String CONN_URL;
    public static String USERNAME;
    public static String PASSWORD;
    public static String DB_NAME;

    private DBConn() {

    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String DBUrl = "jdbc:sqlserver://127.0.0.1:1433;databaseName=" + DB_NAME + ";user=" + USERNAME + ";password=" + PASSWORD;
//            System.out.println(DBUrl);
            conn = DriverManager.getConnection(DBUrl);
        } catch (Exception e) {
            Log.error(e);
        }
        return conn;
    }
}
