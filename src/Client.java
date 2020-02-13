/**
 * Representing Client side
 */

import helper.Constants;
import helper.Range;
import model.Vehicle;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Client {

    private static void displayWelcomeMessage() {
        System.out.println(Constants.WELCOME_STRING);
    }

    private static void displayHelpMessage() {
        System.out.println(Constants.HELP_STRING);
    }

    public static void main(String[] args) {
        displayWelcomeMessage();
        displayHelpMessage();

//        try {
//            HashMap<String, List<Range>> constraints = new HashMap<>();
//            ArrayList<Range> idRange = new ArrayList<>();
//            idRange.add(new Range("" + 4, null));
//
//            constraints.put("id", idRange);
//            System.out.println(Vehicle.findByConstraints(constraints));
//            //System.out.println(Vehicle.findByConstraints("make = Toyota"));
//            //System.out.println(Vehicle.all());
//        } catch (SQLException e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
    }
}
