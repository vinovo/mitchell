/**
 * Model of vehicle objects
 */
package model;

public class Vehicle {
    protected int Id;
    protected Integer Year;
    protected String Make;
    protected String Model;

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

    public String toString() {
        return "Vehicle " + this.Id + "\n" +
                ((this.Year == null) ? "" : "Year: " + this.Year + "\n") +
                ((this.Make == null) ? "" : "Make: " + this.Make + "\n") +
                ((this.Model == null) ? "" : "Model: " + this.Model);
    }
}
