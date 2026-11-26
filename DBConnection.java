package lucentglassmanagement;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1539/orcl";

    private static final String USERNAME = "system";
    private static final String PASSWORD = "*******";

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(
                    URL, USERNAME, PASSWORD);

            System.out.println("Database Connected Successfully!");
            return con;
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}