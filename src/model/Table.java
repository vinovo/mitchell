package model;

import java.sql.SQLException;
import java.util.Map;

/**
 * Object that are a table in the database
 * @param <PK> Type of primary key
 */
public interface Table<PK> {

    abstract String getTableName();

    // Get db schema for creating table
    abstract String getTableSchema();

    // Get primary key value of the table
    abstract String getTablePrimaryKey();

    // Get primary key value of the table
    abstract PK getTablePrimaryKeyValue();

    // Get all tuples
    abstract Map<String, Object> getTuples();

    // Add or update the instance in db
    abstract void writeToDb() throws SQLException;
}