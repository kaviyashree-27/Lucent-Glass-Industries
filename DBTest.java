package lucentglassmanagement;

import java.sql.Connection;

public class DBTest {

    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();

        if (con != null) {
            System.out.println("LUCENT DATABASE READY!");
        } else {
            System.out.println("Please check database settings.");
        }
    }
}