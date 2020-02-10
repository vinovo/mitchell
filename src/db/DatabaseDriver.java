 /**
 * API to access database
 */
 package db;

import model.DBObject;

import java.sql.*;
import java.util.ArrayList;

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

    /**
     * @return Singleton instance of DatabaseDriver
     */
    public static DatabaseDriver getDatabaseDriver() {
        if (DbDriver == null) {
            DbDriver = new DatabaseDriver();
        }

        return DbDriver;
    }

     /**
      * @param object table of objects to be created
      * @return 0 if no error
      */
    public int createTable(DBObject object) {
        String create = "CREATE TABLE IF NOT EXISTS " + object.getDbName() + " (\n" + object.getDbSchema() + ");";

        try {
            Statement stmt = Conn.createStatement();
            stmt.execute(create);
        } catch (SQLException e) {
            System.err.println("Failed to create SQL table for " + object.getDbName() + ":\n" + e.getMessage());
            return e.getErrorCode();
        }

        return 0;
    }

     /**
      * @param object object to be inserted
      * @return 0 if no error
      */
    public int insert(DBObject object) {
        ArrayList<String> keys = object.getDbKeys();
        ArrayList<Object> values = object.getDbValues();
        if (keys.size() == 0 || keys.size() != values.size()) {
            System.err.println("Failed to insert " + object.getDbName() + ", size of keys or values is incorrect");
            return -1;
        }

        StringBuilder sb = new StringBuilder("INSERT INTO " + object.getDbName() + "(");

        for (String key : keys) {
            sb.append(key);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES(");

        for (Object value : values) {
            sb.append(value);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        try {
            Statement stmt = Conn.createStatement();
            stmt.executeUpdate(sb.toString());
        }
        catch (SQLException e) {
            System.err.println("Failed to insert " + object.getDbName() + ":\n" + e.getMessage());
            return e.getErrorCode();
        }

        return 0;
    }

}
