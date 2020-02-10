package model;

import java.util.ArrayList;

public interface DBObject {
    String getDbName();
    // Get db schema for creating table
    String getDbSchema();
    // Get a list of keys for insertion
    ArrayList<String> getDbKeys();
    // Get a list of values for insertion
    ArrayList<Object> getDbValues();
}