package eval.code.quality.utils;

import com.github.javaparser.ast.Node;
import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents the context of the check.
 * <p>It allows to iterate over the different {@code ContentProvider} to launch every checks and report position with the current context</p>
 */
public class Context implements Iterator<ContentProvider> {
    private final Iterator<ContentProvider> itContentProvider;
    private final ContentProvider firstElement;
    private ContentProvider currentContentProvider;

    /**
     * Create a new {@code Context}.
     *
     * @param contentProvider the content provider
     */
    public Context(ContentProvider contentProvider) {
        Preconditions.checkArg(contentProvider != null, "The content provider can not be null");
        this.firstElement = contentProvider;
        this.itContentProvider = contentProvider.iterator();
        this.currentContentProvider = null;
    }

    /**
     * Create a new {@code Context} from another context.
     *
     * @param context the other context for copying the {@code ContentProvider}
     */
    public Context(Context context) {
        this(context.firstElement);
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param node the {@code Node} to get the position from
     * @return the named position
     */
    public Position getPos(Node node) {
        return getPos(node.getBegin().get());
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param line line position
     * @return the named position
     */
    public Position getPos(int line) {
        return getPos(new SinglePosition(line));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param line line position
     * @param column column position
     * @return the named position
     */
    public Position getPos(int line, int column) {
        return getPos(new SinglePosition(line, column));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param position the {@code com.github.javaparser.Position}
     * @return the named position
     */
    public Position getPos(com.github.javaparser.Position position) {
        return getPos(SinglePosition.from(position));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param begin {@code SinglePosition}
     * @param end   {@code SinglePosition}
     * @return the named position
     */
    public Position getPos(SinglePosition begin, SinglePosition end) {
        if (begin.equals(end)) {
            return getPos(begin);
        }
        return getPos(new Range(begin, end));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param lineBegin begin line
     * @param columnBegin begin column
     * @param lineEnd end line
     * @param columnEnd end column
     * @return the named position
     */
    public Position getPos(int lineBegin, int columnBegin, int lineEnd, int columnEnd) {
        if (lineBegin == lineEnd && columnBegin == columnEnd) {
            return getPos(new SinglePosition(lineBegin, columnBegin));
        }
        return getPos(new Range(lineBegin, columnBegin, lineEnd, columnEnd));
    }

    /**
     * Get the range inside the current content provider.
     *
     * @param lineBegin begin line
     * @param lineEnd end line
     * @return the named position
     */
    public Position getRange(int lineBegin, int lineEnd) {
        if (lineBegin == lineEnd) {
            return getPos(new SinglePosition(lineBegin));
        }
        return getPos(new Range(lineBegin, lineEnd));
    }

    /**
     * Get the range inside the current content provider.
     * @param nodes the list of {@code com.github.javaparser.Node} that form a block
     * @return the named position
     */
    public Position getRange(List<? extends Node> nodes) {
        Preconditions.checkArg(nodes != null && !nodes.isEmpty(), "Statements can not be null or empty");
        if (nodes.size() == 1) {
            return getPos(nodes.get(0));
        }
        return getPos(new Range(SinglePosition.from(nodes.get(0).getBegin().get()), SinglePosition.from(nodes.get(nodes.size() - 1).getBegin().get())));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param range the {@code com.github.javaparser.Range}
     * @return the named position
     */
    public Position getPos(com.github.javaparser.Range range) {
        if (range.begin.equals(range.end)) {
            return getPos(SinglePosition.from(range.begin));
        }
        return getPos(Range.from(range));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param positions the list of positions
     * @return the named position
     */
    public Position getPos(List<Position> positions) {
        return getPos(new MultiplePosition(positions));
    }

    /**
     * Get the position inside the current content provider.
     *
     * @param position the position to named
     * @return the named position
     */
    public Position getPos(Position position) {
        return new NamePosition(currentContentProvider.getName(), position);
    }

    @Override
    public boolean hasNext() {
        return itContentProvider.hasNext();
    }

    @Override
    public ContentProvider next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        currentContentProvider = itContentProvider.next();
        return currentContentProvider;
    }

    /**
     * Get the current {@code ContentProvider}.
     *
     * @return the current content provider
     */
    public ContentProvider getContentProvider() {
        return currentContentProvider;
    }
}
