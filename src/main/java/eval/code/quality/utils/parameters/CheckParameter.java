package eval.code.quality.utils.parameters;

import eval.code.quality.checks.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.MultipleContentProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parse command line and JSON "check" parameter.
 */
public class CheckParameter extends RunParameter<List<Check>> {

    private final MultipleContentProvider contentProviders;
    public final static Map<String, Function<ContentProvider, Check>> allPossibleCheck = Map.of("blank lines", BlankLines::new, "braces", Braces::new, "indentation", Indentation::new, "naming", Naming::new);

    public CheckParameter(MultipleContentProvider contentProviders) {
        this.contentProviders = contentProviders;
    }

    @Override
    public List<Check> getValue(CommandLine cmd, JSONObject jsonObject) {
        List<Check> checkList = new ArrayList<>();
        if (isNonNull(cmd.getOptionValues("check"))) {
            if (Arrays.stream(cmd.getOptionValues("check")).anyMatch(e -> e.trim().toLowerCase().equals("all"))) {
                checkList.addAll(allPossibleCheck.values().stream().map(e -> e.apply(contentProviders)).collect(Collectors.toSet()));
            } else {
                checkList.addAll(Arrays.stream(cmd.getOptionValues("check"))
                        .filter(check -> {
                            if (allPossibleCheck.containsKey(check.trim().toLowerCase())) {
                                return true;
                            } else {
                                addWarning("unknown check: \"" + check + "\" ignoring");
                                return false;
                            }
                        }).map(check -> allPossibleCheck.get(check.trim().toLowerCase()).apply(contentProviders)).collect(Collectors.toSet()));
            }
        }
        if (jsonObject.has("check")) {
            if (jsonObject.getJSONArray("check").toList().stream().anyMatch(e -> e.toString().trim().toLowerCase().equals("all"))) {
                checkList.addAll(allPossibleCheck.entrySet().stream()
                        .filter(e -> checkList.stream().noneMatch(c -> c.getName().equals(e.getKey())))
                        .map(e -> e.getValue().apply(contentProviders)).collect(Collectors.toSet()));
            } else {
                checkList.addAll(jsonObject.getJSONArray("check").toList().stream()
                        .filter(check -> {
                            if (allPossibleCheck.containsKey(check.toString().trim().toLowerCase())) {
                                return checkList.stream().noneMatch(c -> c.getName().equals(check.toString()));
                            } else {
                                addWarning("unknown check: \"" + check + "\" ignoring");
                                return false;
                            }
                        }).map(check -> allPossibleCheck.get(check.toString().trim().toLowerCase()).apply(contentProviders)).collect(Collectors.toSet()));
            }
        }
        if (!isNonNull(cmd.getOptionValues("check")) && !jsonObject.has("check")) {
            checkList.addAll(allPossibleCheck.values().stream().map(e -> e.apply(contentProviders)).collect(Collectors.toSet()));
        }
        return checkList;
    }

}
