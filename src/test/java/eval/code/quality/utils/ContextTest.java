package eval.code.quality.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.provider.StringProvider;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class ContextTest {

    @Test void nullProviderThrowsIllegalArgument() {
        ContentProvider contentProvider = null;
        assertThrows(IllegalArgumentException.class, () -> new Context(contentProvider));
    }

    @Test void canCreateSimpleContextFromProvider() {
        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(
                new StringProvider("TEST", "TEST"),
                new StringProvider("TEST2", "TEST2"),
                new StringProvider("TEST3", "TEST3"));
        Context context = new Context(contentProvider);
        assertTrue(context.hasNext());
        assertEquals("TEST", context.next().getName());
        assertEquals("TEST", context.getContentProvider().getName());
        assertTrue(context.hasNext());
        assertEquals("TEST2", context.next().getName());
        assertEquals("TEST2", context.getContentProvider().getName());
        assertTrue(context.hasNext());
        assertEquals("TEST3", context.next().getName());
        assertEquals("TEST3", context.getContentProvider().getName());
        assertFalse(context.hasNext());
        assertThrows(NoSuchElementException.class, context::next);
    }

    @Test void canSetNamedPositionFromContext() {
        Context context = new Context(new StringProvider("name", "test"));
        assertTrue(context.hasNext());
        assertEquals("name", context.next().getName());
        Node node = new IfStmt().setRange(com.github.javaparser.Range.range(com.github.javaparser.Position.pos(1, 1), com.github.javaparser.Position.pos(2,2)));
        assertThat(context.getPos(node), equalTo(
                new NamePosition("name", new SinglePosition(1, 1))
        ));
        assertThat(context.getPos(1), equalTo(
                new NamePosition("name", new SinglePosition(1))
        ));
        assertThat(context.getPos(1, 2), equalTo(
                new NamePosition("name", new SinglePosition(1, 2))
        ));
        com.github.javaparser.Position position = com.github.javaparser.Position.pos(2, 3);
        assertThat(context.getPos(position), equalTo(
                new NamePosition("name", new SinglePosition(2, 3))
        ));
        assertThat(context.getPos(new SinglePosition(2), new SinglePosition(2)), equalTo(
                new NamePosition("name", new SinglePosition(2))
        ));
        assertThat(context.getPos(new SinglePosition(2), new SinglePosition(4)), equalTo(
                new NamePosition("name", new Range(2, 4))
        ));
        assertThat(context.getPos(2, 1, 2, 1), equalTo(
                new NamePosition("name", new SinglePosition(2, 1))
        ));
        assertThat(context.getPos(2, 1, 2, 3), equalTo(
                new NamePosition("name", new Range(2, 1, 2, 3))
        ));
        assertThat(context.getPos(2, 1, 4, 3), equalTo(
                new NamePosition("name", new Range(2, 1, 4, 3))
        ));
        assertThat(context.getRange(2, 2), equalTo(
                new NamePosition("name", new SinglePosition(2))
        ));
        assertThat(context.getRange(2, 4), equalTo(
                new NamePosition("name", new Range(2, 4))
        ));
        assertThat(context.getRange(Collections.singletonList(node)), equalTo(
                new NamePosition("name", new SinglePosition(1, 1))
        ));
        List<Node> list = new ArrayList<>();
        list.add(node);
        list.add(new DoStmt().setRange(com.github.javaparser.Range.range(com.github.javaparser.Position.pos(3, 1), com.github.javaparser.Position.pos(5,2))));
        assertThat(context.getRange(list), equalTo(
                new NamePosition("name", new Range(1, 1, 3, 1))
        ));
        assertThat(context.getPos(com.github.javaparser.Range.range(1, 1, 1,1)), equalTo(
                new NamePosition("name", new SinglePosition(1, 1))
        ));
        assertThat(context.getPos(com.github.javaparser.Range.range(1, 1, 2,3)), equalTo(
                new NamePosition("name", new Range(1, 1, 2, 3))
        ));
        List<Position> listPosition = new ArrayList<>();
        MultiplePosition multiplePosition = new MultiplePosition();
        listPosition.add(new SinglePosition(1));
        multiplePosition.add(new SinglePosition(1));
        listPosition.add(new SinglePosition(3));
        multiplePosition.add(new SinglePosition(3));
        assertThat(context.getPos(listPosition), equalTo(
                new NamePosition("name", multiplePosition)
        ));
        assertThat(context.getPos(new SinglePosition(1)), equalTo(
                new NamePosition("name", new SinglePosition(1))
        ));
    }

    @Test void getPosForNullOrEmptyListOfNodesThrowsIllegalArgument() {
        Context context = new Context(new StringProvider("name", "test"));
        context.next();
        List<Statement> nodes = null;
        assertThrows(IllegalArgumentException.class, () -> context.getRange(nodes));
        List<Statement> nodes2 = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> context.getRange(nodes2));
    }
}
