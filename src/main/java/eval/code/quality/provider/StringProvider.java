package eval.code.quality.provider;

import eval.code.quality.utils.Preconditions;
import eval.code.quality.utils.SCUTuple;

import java.util.NoSuchElementException;

public class StringProvider extends ContentProvider {
    private final SCUTuple tuple;
    private boolean hasNext = true;

    public StringProvider(String content) {
        Preconditions.checkArg(content != null, "String cannot be null");
        this.tuple = new SCUTuple(content);
    }

    @Override
    public boolean _hasNext() {
        return hasNext;
    }

    @Override
    public SCUTuple _next() {
        if(!hasNext) {
            throw new NoSuchElementException();
        }
        hasNext = false;
        return tuple;
    }
}
