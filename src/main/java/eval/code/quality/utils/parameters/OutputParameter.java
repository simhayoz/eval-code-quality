package eval.code.quality.utils.parameters;

import eval.code.quality.utils.ArgParser;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.io.File;

/**
 * Parse command line and JSON "output" parameter.
 */
public class OutputParameter extends RunParameter<File> {

    private static final OutputParameter INSTANCE = new OutputParameter();

    protected OutputParameter() {
    }

    /**
     * Get the instance of {@code OutputParameter}.
     *
     * @return the instance of {@code OutputParameter}
     */
    public static OutputParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public File getValue(CommandLine cmd, JSONObject jsonObject) {
        if (cmd.hasOption("output")) {
            if (cmd.getOptionValue("output").substring(cmd.getOptionValue("output").lastIndexOf(".") + 1).trim().toLowerCase().equals("xml")) {
                if (jsonObject.has("output")) {
                    addDefinedTwice("output");
                }
                return new File(cmd.getOptionValue("output"));
            } else {
                System.out.println("Error: cannot parse to another format than xml");
                ArgParser.getInstance().printCmdHelp();
                System.exit(1);
            }
        }
        if (jsonObject.has("output")) {
            if (jsonObject.getString("output").substring(jsonObject.getString("output").lastIndexOf(".") + 1).trim().toLowerCase().equals("xml")) {
                return new File(jsonObject.getString("output"));
            } else {
                throwErrorJSON("Cannot parse to another format than xml");
            }
        }
        return null;
    }
}
