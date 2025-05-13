package application.bd;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataSource {
    private static Connection conn;

    public static void initializeDataBase() throws DBInitException {
        String driver = "org.postgresql.Driver";
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            throw new DBInitException(e);
        }
    }

    public static Connection getConn() {
        return conn;
    }
}
