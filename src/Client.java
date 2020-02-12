/**
 * Representing Client side
 */

import model.Vehicle;
import java.sql.SQLException;


public class Client {

    public static void main(String[] args) {
        try {
            Vehicle.add(new Vehicle(1, 2017, "Audi", "A8"));
            System.out.println(Vehicle.all());
        } catch (SQLException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}
