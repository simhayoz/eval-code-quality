package eval.code.quality.provider;

import eval.code.quality.utils.Preconditions;
import eval.code.quality.utils.SCUTuple;

import java.util.*;

public class MultipleStringProvider extends ContentProvider {
    private final List<SCUTuple> content = new ArrayList<>();
    private int current = -1;

    public MultipleStringProvider(List<String> content) {
        Preconditions.checkArg(content != null, "List of String cannot be null");
        for(String s: content) {
            this.content.add(new SCUTuple(s));
        }
    }

    @Override
    protected List<SCUTuple> getContent() {
        return Collections.unmodifiableList(content);
    }
}
