package eval.code.quality.provider;

import eval.code.quality.utils.SCUTuple;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a content provider.
 */
public abstract class ContentProvider implements Iterable<SCUTuple> {

    @Override
    public Iterator<SCUTuple> iterator() {
        return getContent().iterator();
    }

    /**
     * Get the content of this {@code ContentProvider}.
     * @return the content of this {@code ContentProvider}
     */
    protected abstract List<SCUTuple> getContent();
}
