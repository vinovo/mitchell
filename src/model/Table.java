package model;

import java.util.Map;

/**
 * Object that are a table in the database
 * @param <PK> Type of primary key
 */
public interface Table<PK> {

    abstract String getTableName();

    // Get db schema for creating table
    abstract String getTableSchema();

    // Get primary key of the table
    abstract PK getTablePrimaryKey();

    // Get all tuples
    abstract Map<String, Object> getTuples();
}