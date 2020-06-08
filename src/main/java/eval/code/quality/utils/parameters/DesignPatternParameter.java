package eval.code.quality.utils.parameters;

import eval.code.quality.checks.*;
import eval.code.quality.checks.pattern.BuilderPattern;
import eval.code.quality.checks.pattern.SingletonPattern;
import eval.code.quality.checks.pattern.VisitorPattern;
import eval.code.quality.provider.MultipleContentProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse JSON "designPattern" parameter.
 */
public class DesignPatternParameter extends RunParameter<List<Check>> {

    private final MultipleContentProvider contentProviders;

    public DesignPatternParameter(MultipleContentProvider contentProviders) {
        this.contentProviders = contentProviders;
    }

    @Override
    public List<Check> getValue(CommandLine cmd, JSONObject jsonObject) {
        List<Check> checks = new ArrayList<>();
        if (jsonObject.has("designPattern")) {
            JSONObject designPattern = jsonObject.getJSONObject("designPattern");
            if (designPattern.has("singleton")) {
                checks.add(new SingletonPattern(contentProviders, designPattern.getString("singleton")));
            }
            if (designPattern.has("builder")) {
                if (designPattern.getJSONObject("builder").has("product") && designPattern.getJSONObject("builder").has("builder")) {
                    checks.add(new BuilderPattern(contentProviders,
                            designPattern.getJSONObject("builder").getString("product"),
                            designPattern.getJSONObject("builder").getString("builder")));
                } else {
                    throwErrorJSON("builder pattern config is not totally defined");
                }
            }
            if (designPattern.has("visitor")) {
                if (designPattern.getJSONObject("visitor").has("visitor")
                        && designPattern.getJSONObject("visitor").has("parent")
                        && designPattern.getJSONObject("visitor").has("children")) {
                    checks.add(new VisitorPattern(contentProviders,
                            designPattern.getJSONObject("visitor").getString("parent"),
                            designPattern.getJSONObject("visitor").getJSONArray("children").toList().stream()
                                    .map(Object::toString).collect(Collectors.toList()),
                            designPattern.getJSONObject("visitor").getString("visitor")));
                } else {
                    throwErrorJSON("visitor pattern config is not totally defined");
                }
            }
        }
        return checks;
    }
}
