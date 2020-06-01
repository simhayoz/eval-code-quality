package eval.code.quality.utils.parameters;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.DirectoryProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryParameter extends RunParameter<List<ContentProvider>> {
    @Override
    public List<ContentProvider> getValue(CommandLine cmd, JSONObject jsonObject) {
        if(isNonNull(cmd.getOptionValues("directory"))) {
            if(jsonObject.has("directory")) {
                addDefinedBoth("directory");
            }
            return Arrays.stream(cmd.getOptionValues("directory"))
                    .filter(dir -> !dir.trim().equals("irectory"))
                    .map(DirectoryProvider::new).collect(Collectors.toList());
        }
        if(jsonObject.has("directory")) {
            return jsonObject.getJSONArray("directory").toList().stream()
                    .map(o -> new DirectoryProvider(o.toString())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
