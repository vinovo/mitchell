 /**
 * API to access database
 */
 package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseDriver {
    private static DatabaseDriver DbDriver = null;
    private static final String url = "jdbc:sqlite:./src/db/vehicles.db";

    private Connection Conn;

    private DatabaseDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
            Conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.err.println("Failed to connect to database:\n" + e.getMessage());
        }
    }

    public static DatabaseDriver getDatabaseDriver() {
        if (DbDriver == null) {
            DbDriver = new DatabaseDriver();
        }

        return DbDriver;
    }
}
