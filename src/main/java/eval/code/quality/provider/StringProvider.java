package eval.code.quality.provider;

import eval.code.quality.utils.Preconditions;
import eval.code.quality.utils.SCUTuple;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class StringProvider extends ContentProvider {
    private final SCUTuple tuple;

    public StringProvider(String content) {
        Preconditions.checkArg(content != null, "String cannot be null");
        this.tuple = new SCUTuple(content);
    }

    @Override
    protected List<SCUTuple> getContent() {
        return Collections.singletonList(tuple);
    }
}
