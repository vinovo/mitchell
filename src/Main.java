/**
 * Main is used as the client side of the project
 */
import db.DatabaseDriver;
import model.Vehicle;


public class Main {

    public static void main(String[] args) {
        DatabaseDriver db = DatabaseDriver.getDatabaseDriver();
        System.out.println(db.insert(new Vehicle(0, null, null, null)));
    }
}
