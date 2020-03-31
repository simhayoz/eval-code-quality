package eval.code.quality.provider;

import eval.code.quality.utils.SCUTuple;

import java.util.Iterator;

/**
 * Represents a content provider.
 */
public abstract class ContentProvider implements Iterable<SCUTuple> {

    /**
     * Check whether the {@code ContentProvider} has a next element.
     *
     * @return whether the {@code ContentProvider} has a next element
     */
    public abstract boolean _hasNext();

    /**
     * Go to the next element.
     *
     * @throws java.util.NoSuchElementException if there is no next element
     */
    public abstract SCUTuple _next();

    @Override
    public Iterator<SCUTuple> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return _hasNext();
            }

            @Override
            public SCUTuple next() {
                return _next();
            }
        };
    }
}
