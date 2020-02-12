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
      */
    public void createTable(String tableName, String schema) throws SQLException {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" + schema + ");";

        Statement stmt = Conn.createStatement();
        stmt.execute(create);
    }

     /**
      * @param object object to be inserted
      */
    public void insert(Table object) throws SQLException {
        StringBuilder insert = new StringBuilder("INSERT INTO " + object.getTableName() + "(");

        Map<String, Object> tuples = object.getTuples();

        for (Map.Entry<String, Object> entry : tuples.entrySet()) {
            String key = entry.getKey();
            insert.append(key);
            insert.append(",");
        }

        insert.deleteCharAt(insert.length() - 1);
        insert.append(") VALUES(");

        for (Map.Entry<String, Object> entry : tuples.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                insert.append("\"").append(value).append("\"");
            } else {
                insert.append(value);
            }

            insert.append(",");
        }

        insert.deleteCharAt(insert.length() - 1);
        insert.append(")");

        Statement stmt = Conn.createStatement();
        stmt.executeUpdate(insert.toString());
    }

    public ResultSet select(String tableName, String tuples, String constraints) throws SQLException {
        if (tuples == null) {
            tuples = "*";
        }

        String select = "SELECT " + tuples + " FROM " + tableName;

        if (constraints != null) {
            select += " WHERE " + constraints;
        }

        Statement stmt = Conn.createStatement();

        return stmt.executeQuery(select);
    }

    public void update(Table object) throws SQLException {
        StringBuilder update = new StringBuilder("UPDATE " + object.getTableName() + " SET");

        Map<String, Object> tuples = object.getTuples();

        for (Map.Entry<String, Object> entry : tuples.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            update.append(' ').append(key).append(" = ");
            if (value instanceof String) {
                update.append("\"").append(value).append("\"");
            } else {
                update.append(value);
            }
            update.append(',');
        }

        update.deleteCharAt(update.length()-1);
        update.append("\nWHERE ").append(object.getTablePrimaryKey()).append(" = ")
                .append(object.getTablePrimaryKeyValue());

        Statement stmt = Conn.createStatement();
        System.out.println(update);
        stmt.executeUpdate(update.toString());
    }

    public void delete(String tableName, String constraints) throws SQLException {
        String delete = "DELETE FROM " + tableName + " WHERE " + constraints;

        Statement stmt = Conn.createStatement();
        stmt.executeUpdate(delete);
    }
}
