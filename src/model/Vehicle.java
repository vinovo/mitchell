/**
 * Model of vehicle objects
 */
package model;


import java.util.ArrayList;

public class Vehicle implements DBObject {
    protected int Id;
    protected Integer Year;
    protected String Make;
    protected String Model;

    private static final String DB_TABLE_NAME = "vehicle";
    private static final String DB_SCHEMA =
            "\tid integer PRIMARY KEY,\n" +
            "\tyear integer,\n" +
            "\tmake text,\n" +
            "\tmodel text\n";

    public Vehicle() {this.Id = -1;}

    public Vehicle(int Id, Integer Year, String Make, String Model) {
        this.Id = Id;
        this.Year = Year;
        this.Make = Make;
        this.Model = Model;
    }

    public int getId() {return this.Id;}

    public int getYear() {return this.Year;}

    public String getMake() {return this.Make;}

    public String getModel() {return this.Model;}

    @Override
    public String getDbName() {
        return DB_TABLE_NAME;
    }

    @Override
    public String getDbSchema() {
        return DB_SCHEMA;
    }

    @Override
    public ArrayList<String> getDbKeys() {
        ArrayList<String> keys = new ArrayList<>();
        StringBuilder sb = new StringBuilder(DB_SCHEMA);
        while(true) {
            int tab = sb.indexOf("\t");
            if (tab == -1)  break;

            sb.delete(0, tab + 1);
            int blank = sb.indexOf(" ");
            keys.add(sb.substring(0, blank));
        }

        return keys;
    }

    @Override
    public ArrayList<Object> getDbValues() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(this.Id);
        values.add(this.Year);
        values.add(this.Make);
        values.add(this.Model);
        return values;
    }

    public String toString() {
        return "Vehicle " + this.Id + "\n" +
                ((this.Year == null) ? "" : "Year: " + this.Year + "\n") +
                ((this.Make == null) ? "" : "Make: " + this.Make + "\n") +
                ((this.Model == null) ? "" : "Model: " + this.Model);
    }
}
