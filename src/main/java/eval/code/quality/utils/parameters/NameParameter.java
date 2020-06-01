package eval.code.quality.utils.parameters;

import eval.code.quality.utils.ArgParser;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

public class NameParameter extends RunParameter<String> {

    @Override
    public String getValue(CommandLine cmd, JSONObject jsonObject) {
        if(cmd.hasOption("name")) {
            if(jsonObject.has("name")) {
                addDefinedBoth("name");
            }
            return cmd.getOptionValue("name");
        } else if(jsonObject.has("name")) {
            return jsonObject.getString("name");
        }
        System.out.println("Error: name not found");
        ArgParser.getInstance().printHelp();
        printJsonDescription();
        System.exit(1);
        return null;
    }

}
