/**
 * Representing Client side
 */

import model.Vehicle;
import java.sql.SQLException;


public class Client {

    public static void main(String[] args) {
        try {
            //Vehicle.create(0, 1999, "Diamond", "Jury").writeToDb();
            System.out.println(Vehicle.all());
        } catch (SQLException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}
