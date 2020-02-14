 /**
 * API to access database
 */
 package db;

import model.Table;

import java.sql.*;
import java.util.Map;

 public class DatabaseHelper {
    // Though the two fields are not final, they're only changeable through the first initialization
    private static boolean TEST_MODE;
    private static String url;

    private static DatabaseHelper DbDriver = null;
    private static final String prod_url = "jdbc:sqlite:./db/vehicles.db";
    // use in-memory db for testing
    private static final String test_url = "jdbc:sqlite::memory:";

    private Connection ongoingConnection;

     private DatabaseHelper() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to database:\n" + e.getMessage());
        }
    }

    /**
     * @return Singleton instance of DatabaseDriver
     */
    public static DatabaseHelper getDatabaseHelperInTestMode() {
        if (DbDriver == null) {
            TEST_MODE = true;
            url = test_url;
            DbDriver = new DatabaseHelper();
        }

        return DbDriver;
    }

     public static DatabaseHelper getDatabaseHelper() {
         if (DbDriver == null) {
             TEST_MODE = false;
             url = prod_url;
             DbDriver = new DatabaseHelper();
         }

         return DbDriver;
     }

    public boolean isTableExisted(String tableName) {
        if (tableName == null || tableName.length() < 1) {
            return false;
        }

        try {
            Connection conn = handleGetConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            handleConnectionClose();
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
        Connection conn = handleGetConnection();
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" + schema + ");";

        Statement stmt = conn.createStatement();
        stmt.execute(create);

        handleConnectionClose();
    }

     /**
      * @param object object to be inserted
      */
    public void insert(Table object) throws SQLException {
        Connection conn = handleGetConnection();
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

        handleConnectionClose();
    }

    public ResultSet select(String tableName, String tuples, String constraints) throws SQLException {
        Connection conn = handleGetConnection();

        if (tuples == null || tuples.length() < 1) {
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
        // undefined behavior
        if (primaryKeyValue == null) {
            return;
        }

        Connection conn = handleGetConnection();
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

        handleConnectionClose();
    }

    public void delete(String tableName, String constraints) throws SQLException {
        // undefined behavior
        if (tableName == null || constraints == null) {
            return;
        }

        Connection conn = handleGetConnection();
        String delete = "DELETE FROM " + tableName + " WHERE " + constraints;

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(delete);

        handleConnectionClose();
    }

    public void drop(String tableName) throws SQLException {
        if (tableName == null) {
            return;
        }

        Connection conn = handleGetConnection();
        String drop = "DROP TABLE IF EXISTS " + tableName + ";";

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(drop);

        handleConnectionClose();
    }

     /**
      * Close ongoing sql connection
      * @throws SQLException any sql exception
      */
    public void closeOngoingConnection() throws SQLException {
        if (ongoingConnection != null) {
            ongoingConnection.close();
            ongoingConnection = null;
        }
    }

     /**
      * Only close connection if in test mode, used in Vehicle class
      * @throws SQLException any sql exception
      */
    public void handleConnectionClose() throws SQLException {
        if (!TEST_MODE) {
            closeOngoingConnection();
        }
    }

    private Connection handleGetConnection() throws SQLException {
        if (ongoingConnection == null) {
            ongoingConnection = DriverManager.getConnection(url);
        }

        return ongoingConnection;
    }
 }
