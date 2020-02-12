 /**
 * API to access database
 */
 package db;

import model.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

 public class DatabaseHelper {
    private static DatabaseHelper DbDriver = null;
    private static final String url = "jdbc:sqlite:./src/db/vehicles.db";

    private Connection Conn;

    private DatabaseHelper() {
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
    public static DatabaseHelper getDatabaseHelper() {
        if (DbDriver == null) {
            DbDriver = new DatabaseHelper();
        }

        return DbDriver;
    }

    public boolean isTableExisted(String tableName) {
        try {
            DatabaseMetaData dbm = Conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            return tables.next();
        } catch (SQLException e) {
            System.err.println("Failed to check table existence:\n" + e.getMessage());
            return false;
        }

    }

     /**
      * @param tableName name of the new table
      * @param schema schema of the new table
      * @return 0 if no error, errorcode otherwise
      */
    public void createTable(String tableName, String schema) throws SQLException {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" + schema + ");";

        Statement stmt = Conn.createStatement();
        stmt.execute(create);
    }

     /**
      * @param object object to be inserted
      * @return 0 if no error
      */
    public void insert(Table object) throws SQLException {
        Map<String, Object> tuples = object.getTuples();
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry: tuples.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }

        if (keys.size() == 0 || keys.size() != values.size()) {
            throw new SQLException("Failed to insert " + object.getTableName() +
                    ", size of keys or values is incorrect");
        }

        StringBuilder sb = new StringBuilder("INSERT INTO " + object.getTableName() + "(");

        for (String key : keys) {
            sb.append(key);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES(");

        for (Object value : values) {
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }

            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        Statement stmt = Conn.createStatement();
        stmt.executeUpdate(sb.toString());
    }

    public ResultSet select(String tableName, String tuples, String conditions) throws SQLException {
        if (tuples == null) {
            tuples = "*";
        }

        String select = "SELECT " + tuples + " FROM " + tableName;

        if (conditions != null) {
            select += " WHERE " + conditions;
        }

        Statement stmt = Conn.createStatement();

        return stmt.executeQuery(select);
    }


}
