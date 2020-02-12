/**
 * Representing Client side
 */

import model.Vehicle;
import java.sql.SQLException;


public class Client {

    public static void main(String[] args) {
        try {
            Vehicle.updateById(0, Vehicle.create(2, 2000, "Mama", "Mia"));
            //System.out.println(Vehicle.findByConstraints("make = Toyota"));
            System.out.println(Vehicle.all());
        } catch (SQLException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}
