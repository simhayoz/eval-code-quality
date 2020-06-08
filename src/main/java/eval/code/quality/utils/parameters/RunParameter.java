package eval.code.quality.utils.parameters;

import eval.code.quality.utils.ArgParser;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

/**
 * Base class for parsing argument.
 *
 * @param <T> the type of return of the parsed element
 */
public abstract class RunParameter<T> {

    /**
     * Get the value associated to the argument.
     *
     * @param cmd        the {@code CommandLine} for command line parameter
     * @param jsonObject the {@code JSONObject} for json config parameter
     * @return the parsed element value
     */
    public abstract T getValue(CommandLine cmd, JSONObject jsonObject);

    /**
     * Checks if the list of string is non-null and non-empty.
     *
     * @param strings the list of string to check
     * @return whether the list of string is non-null and non-empty
     */
    protected boolean isNonNull(String[] strings) {
        return strings != null && strings.length > 0;
    }

    /**
     * Add warning that type is defined in both command line and JSON config.
     *
     * @param type the type of element that was defined twice
     */
    protected void addDefinedTwice(String type) {
        addWarning(type + " defined in JSON and command line, ignoring JSON value");
    }

    /**
     * Print warning while parsing argument
     *
     * @param warning content of the warning
     */
    protected void addWarning(String warning) {
        System.out.println("Warning: " + warning);
    }

    /**
     * Print error message and JSON config helper and exit.
     *
     * @param error the error to print
     */
    protected void throwErrorJSON(String error) {
        System.out.println("Error: " + error);
        ArgParser.getInstance().printJSONHelp();
        System.exit(1);
    }
}
