package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Con {
    private static final String URL = "jdbc:mysql://localhost:3306/examination_system";
    private static final String USER = "Mshayan";
    private static final String PASSWORD = "shayan1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
