package helper;

public class Constants {
    private Constants() {}

    public final static String WELCOME_STRING =
            "Welcome to Vehicle Finder!\n" +
            "Enter a command or type \"help\" for usage. Have fun with it :-)";

    public final static String HELP_STRING =
            "USAGE:\n" +
            "Fetch all vehicles info:\n" + "GET ALL\n" +
            "Fetch vehicle info by ID:\n" + "GET [ID]\n" +
            "Fetch vehicle by custom constraints:\n" + "GET CUSTOM\n" +
            "Update vehicle by id:\n" + "SET [ID]\n" +
            "*Note: if [ID] does not exist, then a new vehicle info will be added to our database.\n" +
            "Delete vehicle by id:\n" + "DELETE [ID]\n" +
            "To clear all the records: CLEAR\n" +
            "To exit the program: EXIT";

    public final static String ERROR_INVALID_COMMAND_STRING =
            "Error: Invalid Command! Type \"help\" to see a list of supported command.";

    public final static String ERROR_DB_ERR_STRING =
            "ERROR: Failed to fetch information from the database, please try again";

    public final static String ERROR_DB_INVALID_CONSTRAINT_STRING =
            "ERROR: Invalid constraint set, please make sure the values are correct";

    public final static String ERROR_INVALID_VALUE_STRING =
            "ERROR: One or more invalid values have been entered, please make sure the values are correct";

    public final static char DOUBLE_QUOTES_CHAR = '\"';
}
