package eval.code.quality.utils.parameters;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.DirectoryProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse command line and JSON "directory" parameter.
 */
public class DirectoryParameter extends RunParameter<List<ContentProvider>> {

    private static final DirectoryParameter INSTANCE = new DirectoryParameter();

    private DirectoryParameter() {
    }

    /**
     * Get the instance of {@code DirectoryParameter}.
     *
     * @return the instance of {@code DirectoryParameter}
     */
    public static DirectoryParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ContentProvider> getValue(CommandLine cmd, JSONObject jsonObject) {
        if (isNonNull(cmd.getOptionValues("directory"))) {
            if (jsonObject.has("directory")) {
                addDefinedTwice("directory");
            }
            return Arrays.stream(cmd.getOptionValues("directory"))
                    // Remove "irectory" as it is considered a parameter like -d=irectory instead of -directory
                    .filter(dir -> !dir.trim().equals("irectory"))
                    .map(DirectoryProvider::new).collect(Collectors.toList());
        }
        if (jsonObject.has("directory")) {
            return jsonObject.getJSONArray("directory").toList().stream()
                    .map(o -> new DirectoryProvider(o.toString())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
