 /**
 * API to access database
 */
 package db;

import model.Table;

import java.sql.*;
import java.util.Map;

 public class DatabaseHelper {
    private static DatabaseHelper DbDriver = null;
    private static final String url = "jdbc:sqlite:./src/db/vehicles.db";
    private Connection ongoingConnection = null;

    private DatabaseHelper() {
        try {
            Class.forName("org.sqlite.JDBC");
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
            Connection conn = DriverManager.getConnection(url);
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            conn.close();
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
        Connection conn = DriverManager.getConnection(url);
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" + schema + ");";

        Statement stmt = conn.createStatement();
        stmt.execute(create);

        conn.close();
    }

     /**
      * @param object object to be inserted
      */
    public void insert(Table object) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
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

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(insert.toString());

        conn.close();
    }

    public ResultSet select(String tableName, String tuples, String constraints) throws SQLException {
        Connection conn = DriverManager.getConnection(url);

        if (tuples == null) {
            tuples = "*";
        }

        String select = "SELECT " + tuples + " FROM " + tableName;

        if (constraints != null) {
            select += " WHERE " + constraints;
        }

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(select);

        // To be closed later
        ongoingConnection = conn;

        return result;
    }

    public void update(String primaryKeyValue, Table object) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
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
                .append(primaryKeyValue);

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(update.toString());

        conn.close();
    }

    public void delete(String tableName, String constraints) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        String delete = "DELETE FROM " + tableName + " WHERE " + constraints;

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(delete);

        conn.close();
    }

    public void drop(String tableName) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        String drop = "DROP TABLE IF EXISTS " + tableName + ";";

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(drop);

        conn.close();
    }

    public void closeOngoingConnection() throws SQLException {
        if (ongoingConnection != null)
            ongoingConnection.close();
    }
}
