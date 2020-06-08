package eval.code.quality.utils.parameters;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse command line and JSON "file" parameter.
 */
public class FileParameter extends RunParameter<List<ContentProvider>> {

    private static final FileParameter INSTANCE = new FileParameter();

    private FileParameter() {
    }

    /**
     * Get the instance of {@code FileParameter}.
     *
     * @return the instance of {@code FileParameter}
     */
    public static FileParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ContentProvider> getValue(CommandLine cmd, JSONObject jsonObject) {
        if (isNonNull(cmd.getOptionValues("file"))) {
            if (jsonObject.has("file")) {
                addDefinedTwice("file");
            }
            return Arrays.stream(cmd.getOptionValues("file"))
                    .map(f -> new FileProvider(new File(f))).collect(Collectors.toList());
        }
        if (jsonObject.has("file")) {
            return jsonObject.getJSONArray("file").toList().stream()
                    .map(f -> new FileProvider(new File(f.toString()))).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
