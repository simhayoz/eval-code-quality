package eval.code.quality.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

public class TupleTest {
    Tuple<String, Integer> tuple;

    @BeforeEach void initTuple() {
        tuple = new Tuple<>("test", 3);
    }

    @Test void canCreateSimpleTuple() {
        assertThat(tuple._1, is("test"));
        assertThat(tuple._2, is(3));
    }

    @Test void canAcceptFunction() {
        ForTest forTest = new ForTest();
        tuple.accept((l, r) -> forTest.wasCalled = true);
        assertTrue(forTest.wasCalled);
    }

    @Test void canApplyFunction() {
        assertThat(tuple.apply((l, r) -> l + r), is("test3"));
    }

    @Test void canMapTuple() {
        Tuple<Boolean, String> newTuple = tuple.map(s -> s.equals("test"), i -> i + "2");
        assertThat(newTuple._1, is(true));
        assertThat(newTuple._2, is("32"));
    }

    @Test void equalsWork() {
        Tuple<String, Integer> other = new Tuple<>("test", 3);
        Tuple<Integer, Integer> different = new Tuple<>(4, 3);
        assertEquals(tuple, tuple);
        assertEquals(tuple, other);
        assertNotEquals(tuple, different);
        assertNotEquals(tuple, null);
    }

    @Test void toStringReturnRightContent() {
        assertThat(tuple.toString(), is("(test,3)"));
    }

    private static class ForTest {
        public boolean wasCalled = false;
    }
}
