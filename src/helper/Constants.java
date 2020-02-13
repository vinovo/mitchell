package helper;

public class Constants {
    private Constants() {}

    public final static String WELCOME_STRING = "Welcome to Vehicle Finder!\n" +
            "Enter a command or type \"help\" for usage. Have fun with it :-)";

    public final static String HELP_STRING = "USAGE:\n" +
            "Fetch all vehicles info:\n" + "GET ALL\n" +
            "Fetch vehicle info by ID:\n" + "GET [ID]\n" +
            "Fetch vehicle by custom constraints:\n" + "GET CUSTOM\n" +
            "Update vehicle by id:\n" + "SET [ID]\n" +
            "Note if [ID] does not exist, then a new vehicle info will be added to our database.\n";

    public final static char DOUBLE_QUOTES_CHR = '\"';
}
