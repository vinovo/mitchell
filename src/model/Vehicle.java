/**
 * Model of vehicle objects
 */
package model;


import db.DatabaseHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vehicle implements Table<Integer> {
    private int id;
    private Integer year;
    private String make;
    private String model;

    private static final DatabaseHelper DB = DatabaseHelper.getDatabaseHelper();
    private static final String DB_TABLE_NAME = "vehicle";
    private static final String DB_SCHEMA =
            "\tid integer PRIMARY KEY,\n" +
            "\tyear integer,\n" +
            "\tmake text,\n" +
            "\tmodel text,\n";

    private static boolean TABLE_EXISTS = checkTableExistence();

    public Vehicle(int id, Integer year, String make, String model) {
        this.id = id;
        this.year = year;
        this.make = make;
        this.model = model;
    }

    private static boolean checkTableExistence() {
        return DB.isTableExisted(DB_TABLE_NAME);
    }

    /**
     * @return null if error, all Vehicles object in the db otherwise
     */
    public static List<Vehicle> all() throws SQLException {
        if (!TABLE_EXISTS) {
            System.err.println("Failed to fetch all, table " + DB_TABLE_NAME + " does not exist");
            return null;
        }

        ResultSet result = DB.select(DB_TABLE_NAME, "*", null);
        ArrayList<Vehicle> vehicles = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt("id");
            int year = result.getInt("year");
            String make = result.getString("make");
            String model = result.getString("model");

            vehicles.add(new Vehicle(id, year, make, model));
        }

        return vehicles;
    }

    /**
     * @param id id of the vehicle to find
     * @return a new vehicle object if found, null otherwise
     */
    public static Vehicle findById(int id) throws SQLException {
        if (!TABLE_EXISTS) {
            System.err.println("Failed to find, table " + DB_TABLE_NAME + " does not exist");
            return null;
        }

        ResultSet result = DB.select(DB_TABLE_NAME, "*", "id = " + id);

        if (result.next()) {
            int year = result.getInt("year");
            String make = result.getString("make");
            String model = result.getString("model");

            return new Vehicle(id, year, make, model);
        } else {
            return null;
        }
    }

    /**
     * Delete the instance with id
     * @param id id of the vehicle to delete
     * @return 0 if success, errorcode otherwise
     */
    public static void deleteById(int id) throws SQLException {
        if (!TABLE_EXISTS) {
            System.err.println("Failed to delete, table " + DB_TABLE_NAME + " does not exist");
            return;
        }

    }

    /**
     * @param obj instance of the vehicle to be added
     * @return 0 if success, errorcode otherwise
     */
    public static void add(Vehicle obj) throws SQLException {
        if (!TABLE_EXISTS) {
            DB.createTable(DB_TABLE_NAME, DB_SCHEMA);
            TABLE_EXISTS = true;
        }

        DB.insert(obj);
    }

    /**
     * update vehicle with the same id in the db
     * @param obj the new instance
     * @return 0 if success, errorcode otherwise
     */
    public static void update(Vehicle obj) throws SQLException {
        if (!TABLE_EXISTS) {
            System.err.println("Failed to update, table " + DB_TABLE_NAME + " does not exist");
            return;
        }

    }

    // Getter
    public int getId() {return id;}
    public int getYear() {return year;}
    public String getMake() {return make;}
    public String getModel() {return model;}

    // Mutator
    public void setId(int id) {this.id = id;}
    public void setYear(int year){this.year = year; }
    public void setMake(String make) {this.make= make;}
    public void setModel(String model) {this.model = model;}

    @Override
    public String getTableName() {
        return DB_TABLE_NAME;
    }

    @Override
    public String getTableSchema() {
        return DB_SCHEMA;
    }

    @Override
    public Integer getTablePrimaryKey() {
        return id;
    }

    @Override
    public Map<String, Object> getTuples() {
        Map<String, Object> tuples = new HashMap<>();
        tuples.put("id", id);
        tuples.put("year", year);
        tuples.put("make", make);
        tuples.put("model", model);

        return tuples;
    }


    public String toString() {
        return getTuples().toString();
    }
}
