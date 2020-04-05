package eval.code.quality.utils;

import com.github.javaparser.ast.Node;
import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents the context of the test.
 * <p>It allows to iterate over the different {@code ContentProvider} to launch every tests and report position with the current context</p>
 */
public class Context {
    private Iterator<ContentProvider> itContentProvider;
    private final ContentProvider firstElement;
    private ContentProvider currentContentProvider;

    public Context(ContentProvider contentProvider) {
        this.firstElement = contentProvider;
        this.itContentProvider = contentProvider.iterator();
        this.currentContentProvider = null;
    }

    public Context(Context context) {
        this(context.firstElement);
    }

    public NamePosition setPos(Node node) {
        return setPos(node.getBegin().get());
    }

    public NamePosition setPos(int line) {
        return setPos(new SinglePosition(line));
    }

    public NamePosition setPos(int line, int column) {
        return setPos(new SinglePosition(line, column));
    }

    public NamePosition setPos(com.github.javaparser.Position position) {
        return setPos(SinglePosition.from(position));
    }

    public NamePosition setPos(SinglePosition begin, SinglePosition end) {
        if (begin.equals(end)) {
            return setPos(begin);
        }
        return setPos(new Range(begin, end));
    }

    public NamePosition setPos(int lineBegin, int columnBegin, int lineEnd, int columnEnd) {
        if (lineBegin == lineEnd && columnBegin == columnEnd) {
            return setPos(new SinglePosition(lineBegin, columnBegin));
        }
        return setPos(new Range(lineBegin, columnBegin, lineEnd, columnEnd));
    }

    public NamePosition setRange(int lineBegin, int lineEnd) {
        if(lineBegin == lineEnd) {
            return setPos(new SinglePosition(lineBegin));
        }
        return setPos(new Range(lineBegin, lineEnd));
    }

    public NamePosition setPos(com.github.javaparser.Range range) {
        if(range.begin.equals(range.end)) {
            return setPos(SinglePosition.from(range.begin));
        }
        return setPos(Range.from(range));
    }

    public NamePosition setPos(List<Position> positions) {
        return setPos(new MultiplePosition(positions));
    }

    public NamePosition setPos(Position position) {
        return new NamePosition(currentContentProvider.getName(), position);
    }

    public boolean hasNextProvider() {
        return itContentProvider.hasNext();
    }

    public ContentProvider nextProvider() {
        if(!hasNextProvider()) {
            throw new NoSuchElementException();
        }
        currentContentProvider = itContentProvider.next();
        return currentContentProvider;
    }

    public ContentProvider getContentProvider() {
        return currentContentProvider;
    }
}
