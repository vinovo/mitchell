/**
 * Representing Client side
 */

import helper.Range;
import model.Vehicle;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Client {

    public static void main(String[] args) {
        try {
            HashMap<String, List<Range>> constraints = new HashMap<>();
            ArrayList<Range> idRange = new ArrayList<>();
            idRange.add(new Range("Audi", "Audi"));

            constraints.put("make", idRange);
            System.out.println(Vehicle.findByConstraints(constraints));
            //System.out.println(Vehicle.findByConstraints("make = Toyota"));
            //System.out.println(Vehicle.all());
        } catch (SQLException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}
