package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    public static final String DB_URL_FORMAT = "jdbc:mysql://%s:%d/%s?useUnicode=true&contentEncoding=UTF-8&serverTimezone=UTC";
    public static final String DB_SERVER = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "servlet_exam";
    public static final String DB_USER = "root";
    public static final String DB_PW = "";

    private static Connection instance;

    private ConnectionHelper() {

    }

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver ());
            if (instance == null || instance.isClosed()) {
                instance = DriverManager.getConnection(
                        String.format(DB_URL_FORMAT, DB_SERVER, DB_PORT, DB_NAME),
                        DB_USER,
                        DB_PW);
                System.out.println("Connect Success !!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return instance;
    }
}
