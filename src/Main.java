import db.DatabaseDriver;

/**
 * Main driver of the program, representing client side
 */

public class Main {

    public static void main(String[] args) {
        DatabaseDriver db = DatabaseDriver.getDatabaseDriver();
    }
}
