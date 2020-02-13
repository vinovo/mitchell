/**
 * Representing Client side
 */

import db.DatabaseHelper;
import helper.Constants;
import helper.Range;
import model.Vehicle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.exit;


public class Client {

    private static final int CODE_NORMAL = 0;
    private static final int CODE_ERROR = -1;
    private static final int CODE_EXIT = 1;

    private static void displayWelcomeMessage() {
        System.out.println(Constants.WELCOME_STRING);
    }

    private static void displayHelpMessage() {
        System.out.println(Constants.HELP_STRING);
    }

    /**
     * @return true if no error
     */
    private static boolean printAllVehicles() {
        try {
            List<Vehicle> vehicles = Vehicle.all();

            // To suppress warning, this will never be executed
            if (vehicles == null)
                throw new SQLException();

            for (Vehicle vehicle : vehicles) {
                System.out.println(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(Constants.ERROR_DB_ERR_STRING);
            return false;
        }

        return true;
    }

    /**
     * @param reader reader to keep on reading lines
     * @return 0 if no error, -1 if error, 1 if exiting
     */
    private static int takeConstraint(BufferedReader reader) throws IOException {
        String[] fields = Vehicle.getFields();
        HashMap<String, List<Range>> constraint = new HashMap<>();

        for (String field : fields) {
            System.out.println("Enter range (inclusive) for " + field + " (hit enter to skip)");
            constraint.put(field, new ArrayList<>());
            String response;
            do {
                System.out.print("Lower bound: ");
                String lower = reader.readLine();

                boolean skipLower = (lower == null || lower.length() == 0 || lower.indexOf(' ') >= 0);
                if (skipLower) {
                    System.out.println("Skipping lower bound for " + field);
                } else if (lower.toLowerCase().equals("exit"))    return CODE_EXIT;

                System.out.print("Upper bound: ");
                String upper = reader.readLine();

                boolean skipUpper = (upper == null || upper.length() == 0 || upper.indexOf(' ') >= 0);
                if (skipUpper) {
                    System.out.println("Skipping upper bound for " + field);
                } else if (upper.toLowerCase().equals("exit"))  return CODE_EXIT;

                if (skipLower && skipUpper) break;

                constraint.get(field).add(new Range(lower, upper));

                System.out.print("Add another constraint (Y/N)? ");
                response = reader.readLine();
                if (response != null) {
                    response = response.toLowerCase();
                    if (response.equals("exit"))    return CODE_EXIT;
                } else {
                    break;
                }
            } while (response.equals("y") || response.equals("yes"));
        }

        try {
            List<Vehicle> vehicles = Vehicle.findByConstraints(constraint);

            for (Vehicle vehicle : vehicles) {
                System.out.println(vehicle);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                System.out.println(Constants.ERROR_DB_INVALID_CONSTRAINT_STRING);
            } else {
                System.out.println(Constants.ERROR_DB_ERR_STRING);
            }

            return CODE_ERROR;
        }

        return CODE_NORMAL;
    }

    /**
     * @param arg the id to be parsed
     * @return true if no error
     */
    private static boolean parseId(String arg) {
        try {
            int id = Integer.parseInt(arg);
            Vehicle vehicle = Vehicle.findById(id);
            if (vehicle == null) {
                System.out.println("Vehicle " + id + " not found.");
            } else {
                System.out.println(vehicle);
            }
        } catch (NumberFormatException e) {
            System.out.println(Constants.ERROR_INVALID_COMMAND_STRING);
            return false;
        } catch (SQLException e) {
            System.out.println(Constants.ERROR_DB_ERR_STRING);
            return false;
        }
        return true;
    }

    /**
     * @param arg id of the vehicle to be updated or created
     * @return 0 if no error, -1 if error, 1 if exiting
     */
    private static int handleSet(String arg, BufferedReader reader) throws IOException {
        int id;
        try {
            id = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.out.println(Constants.ERROR_INVALID_COMMAND_STRING);
            return CODE_ERROR;
        }

        try {
            Vehicle vehicle = Vehicle.findById(id);
            boolean creatingNew = vehicle == null;
            String[] fields = Vehicle.getFields();
            String[] values = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                System.out.print("Enter a new value for " + fields[i] +
                        " (enter nothing to set empty value): ");
                String value = reader.readLine();

                if (value != null) {
                    if (value.toLowerCase().equals("exit"))
                        return CODE_EXIT;
                    else if (value.length() == 0)
                        value = null;
                }

                values[i] = value;
            }

            int newId = values[0] == null ? Integer.parseInt(arg) : Integer.parseInt(values[0]);
            Integer year = Integer.parseInt(values[1]);
            if (year < 1950 || year > 2050) {
                System.out.println("Value of year has to be in range [1950, 2050].");
                return CODE_ERROR;
            }

            String make = values[2];
            if (make == null) {
                System.out.println("Value of make must be provided.");
                return CODE_ERROR;
            }

            String model = values[3];
            if (model == null) {
                System.out.println("Value of model must be provided");
                return CODE_ERROR;
            }

            Vehicle newVehicle = Vehicle.create(newId, year, make, model);

            if (creatingNew) {
                newVehicle.writeToDb();
                System.out.println("New vehicle added: ");
            } else {
                Vehicle.updateById(id, newVehicle);
                System.out.println("Vehicle " + id + " updated to: ");
            }
            System.out.println(newVehicle);

        } catch (NumberFormatException e) {
            System.out.println(Constants.ERROR_INVALID_VALUE_STRING);
            return CODE_ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(Constants.ERROR_DB_ERR_STRING);
            return CODE_ERROR;
        }

        return CODE_NORMAL;
    }

    private static void clearDb() {
        try {
            Vehicle.deleteAll();
            System.out.println("All records have been cleared");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to clear the database, please try again.");
        }
    }

    /**
     * @param arg the id of the vehicle
     * @return true if no error
     */
    private static boolean delete(String arg) {
        try {
            int id = Integer.parseInt(arg);
            Vehicle.deleteById(id);
            System.out.println("Vehicle " + arg + " has been deleted");
        } catch (NumberFormatException e) {
            System.out.println(Constants.ERROR_INVALID_COMMAND_STRING);
            return false;
        } catch (SQLException e) {
            System.out.println("Failed to delete Vehicle " + arg);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        displayWelcomeMessage();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        int error;

        try {
            while (true) {
                if (Vehicle.isTableExist()) {
                    System.out.println("Enter a command: ");
                } else {
                    System.out.println("There's no record in the database yet, " +
                            "please use SET command to add vehicle info");
                }
                line = reader.readLine();
                if (line == null)   break;
                else    line = line.toLowerCase();
                if (line.equals("exit"))    break;
                if (line.equals("help")) {
                    displayHelpMessage();
                    continue;
                }

                if (line.equals("clear")) {
                    clearDb();
                    continue;
                }

                String[] parts = line.split(" ");

                if (parts.length != 2) {
                    System.out.println(Constants.ERROR_INVALID_COMMAND_STRING);
                    continue;
                }

                String command = parts[0];
                String arg = parts[1];

                if (command.equals("get")) {
                    if (arg.equals("all")) {
                        if (!printAllVehicles())
                            continue;
                    } else if (arg.equals("custom")) {
                        error = takeConstraint(reader);
                        if (error == CODE_ERROR)
                            continue;
                        else if (error == CODE_EXIT) {
                            break;
                        }
                    } else {
                        if (!parseId(arg))
                            continue;
                    }
                } else if (command.equals("set")) {
                    error = handleSet(arg, reader);
                    if (error == CODE_ERROR)
                        continue;
                    else if (error == CODE_EXIT)
                        break;
                } else if (command.equals("delete")) {
                    if (!delete(arg))
                        continue;
                }
                else {
                    System.out.println(Constants.ERROR_INVALID_COMMAND_STRING);
                }
            }


        } catch (IOException e) {
            System.out.println("Oops, we encountered some problem reading your input.");
            exit(-1);
        }
    }
}
