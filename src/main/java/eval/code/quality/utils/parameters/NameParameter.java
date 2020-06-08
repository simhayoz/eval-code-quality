package eval.code.quality.utils.parameters;

import eval.code.quality.utils.ArgParser;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

/**
 * Parse command line and JSON "name" parameter.
 */
public class NameParameter extends RunParameter<String> {

    private static final NameParameter INSTANCE = new NameParameter();

    protected NameParameter() {
    }

    /**
     * Get the instance of {@code NameParameter}.
     *
     * @return the instance of {@code NameParameter}
     */
    public static NameParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public String getValue(CommandLine cmd, JSONObject jsonObject) {
        if (cmd.hasOption("name")) {
            if (jsonObject.has("name")) {
                addDefinedTwice("name");
            }
            return cmd.getOptionValue("name");
        } else if (jsonObject.has("name")) {
            return jsonObject.getString("name");
        }
        ArgParser.getInstance().printCmdHelp();
        throwErrorJSON("name not found");
        return null;
    }

}
