/**
 * Model of vehicle objects
 */
package model;


import db.DatabaseHelper;
import helper.Range;

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
            "\tmake text NOT NULL,\n" +
            "\tmodel text NOT NULL,\n" +
            "\tCHECK (year >= 1950 AND year <= 2050 AND length(make) > 0 AND length(model) > 0)";

    private static boolean TABLE_EXISTS = isTableExist();

    private Vehicle(int id, Integer year, String make, String model) {
        this.id = id;
        this.year = year;
        this.make = make;
        this.model = model;
    }

    public static boolean isTableExist() {
        return DB.isTableExisted(DB_TABLE_NAME);
    }

    /**
     * @return null if error, all Vehicles object in the db otherwise
     */
    public static List<Vehicle> all() throws SQLException {
        ResultSet result = DB.select(DB_TABLE_NAME, "*", null);
        List<Vehicle> ret = parseResult(result);
        DB.handleConnectionClose();

        return ret;
    }

    /**
     * @param id id of the vehicle to find
     * @return a new vehicle object if found, null otherwise
     */
    public static Vehicle findById(int id) throws SQLException {
        if (!TABLE_EXISTS) {
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
     * @param constraints constraints in SQL format
     * @return List of qualified vehicles
     * @throws SQLException any SQL exception.
     */
    public static List<Vehicle> findByConstraints(HashMap<String, List<Range>> constraints) throws SQLException {
        String constraintString = parseConstraintList(constraints);

        ResultSet result = DB.select(DB_TABLE_NAME, "*", constraintString);
        List<Vehicle> ret = parseResult(result);
        DB.handleConnectionClose();

        return ret;
    }

    private static String parseConstraintList(HashMap<String, List<Range>> constraints) {
        if (constraints == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // Parsing constraints for id
        String key = "id";

        parseRanges(constraints, sb, key, false);

        // Parsing year constraints
        key = "year";
        parseRanges(constraints, sb, key, false);

        // Parsing make constraints
        key = "make";
        parseRanges(constraints, sb, key, true);

        key = "model";
        parseRanges(constraints, sb, key, true);

        // Delete trailing " AND "
        if (sb.length() > 5) {
            sb.delete(sb.length() - 5, sb.length());
        }

        return sb.toString();
    }

    private static void parseRanges(HashMap<String, List<Range>> constraints, StringBuilder sb, String key,
                                    boolean isString) {
        List<Range> lst = constraints.get(key);
        if (lst != null && lst.size() > 0) {
            int startAt = sb.length();
            for (Range range : lst) {
                if (range == null)  continue;
                sb.append(range.generateConstraintString(key, isString)).append(" OR ");
            }

            if (sb.length() > 4 && sb.lastIndexOf(" OR ") == sb.length() - 4) {
                sb.delete(sb.length() - 4, sb.length());
                sb.insert(startAt, '(');
                sb.append(") AND ");
            }
        }
    }

    private static List<Vehicle> parseResult(ResultSet result) throws SQLException {
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
     * Delete the instance with id
     * @param id id of the vehicle to delete
     */
    // Could potentially implement another deleteByConstraint(Map<String, List<Constraint>> constraints)
    public static void deleteById(int id) throws SQLException {
        DB.delete(DB_TABLE_NAME, "id = " + id);
    }


    /**
     * update vehicle with the same id in the db
     */
    public static void updateById(int id, Vehicle obj) throws SQLException {
        DB.update("" + id, obj);
    }

    public static Vehicle create(int id, Integer year, String make, String model) {
        return new Vehicle(id, year, make, model);
    }

    // drop vehicle table from the db
    public static void deleteAll() throws SQLException {
        DB.drop(DB_TABLE_NAME);
        TABLE_EXISTS = false;
    }

    public static String[] getFields() {
        return new String[]{"id", "year", "make", "model"};
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
    public String getTablePrimaryKey() {
        return "id";
    }

    @Override
    public Integer getPrimaryKeyValue() {
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

    @Override
    public void writeToDb() throws SQLException {
        if (!TABLE_EXISTS) {
            DB.createTable(DB_TABLE_NAME, DB_SCHEMA);
            TABLE_EXISTS = true;
        }

        try {
            DB.insert(this);
        } catch (SQLException e) {
            // if already exists in the db
            if (e.getErrorCode() == 19) {
                DB.update("" + getPrimaryKeyValue(), this);
            } else {
                throw e;
            }
        }
    }

    public String toString() {
        return "Vehicle " + this.id + ": Year " + this.year + ", Make " + this.make + ", Model " + this.model;
    }
}
