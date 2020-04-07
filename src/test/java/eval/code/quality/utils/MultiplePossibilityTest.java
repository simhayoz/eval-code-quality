package eval.code.quality.utils;

import eval.code.quality.position.Position;
import eval.code.quality.position.SinglePosition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiplePossibilityTest {

    @Test void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> MultiplePossibility.at(null, "description"));
        assertThrows(IllegalArgumentException.class, () -> MultiplePossibility.at(new HashMap<>(), null));
        List<Position> nullPosition = null;
        assertThrows(IllegalArgumentException.class, () -> MultiplePossibility.at(nullPosition));
        Map<Position, String> nullMapPosition = null;
        assertThrows(IllegalArgumentException.class, () -> MultiplePossibility.at(nullMapPosition));
    }

    @Test void canCreateFromAt() {
        Map<Position, String> map = new HashMap<>();
        map.put(new SinglePosition(0), "");
        map.put(new SinglePosition(1), "");
        map.put(new SinglePosition(2), "");
        MultiplePossibility multiplePossibility = MultiplePossibility.at(new ArrayList<>(map.keySet()));
        assertThat(multiplePossibility.positions, equalTo(map));
        assertThat(multiplePossibility.description, equalTo(""));
        map = new HashMap<>();
        map.put(new SinglePosition(0), "1");
        map.put(new SinglePosition(1), "2");
        map.put(new SinglePosition(2), "3");
        multiplePossibility = MultiplePossibility.at(map);
        assertThat(multiplePossibility.positions, equalTo(map));
        assertThat(multiplePossibility.description, equalTo(""));
        map = new HashMap<>();
        map.put(new SinglePosition(0), "1");
        map.put(new SinglePosition(1), "2");
        map.put(new SinglePosition(2), "3");
        multiplePossibility = MultiplePossibility.at(map, "description");
        assertThat(multiplePossibility.positions, equalTo(map));
        assertThat(multiplePossibility.description, equalTo("description"));
    }

    @Test void toStringWorkForSimpleError() {
        Map<Position, String> map = new HashMap<>();
        map.put(new SinglePosition(0), "1");
        map.put(new SinglePosition(1), "2");
        MultiplePossibility multiplePossibility = MultiplePossibility.at(map, "description");
        assertThat(multiplePossibility.toString(), anyOf(equalTo("description but was: \n (line 0): 1\n (line 1): 2\n"),
                equalTo("description but was: \n (line 1): 2\n (line 0): 1\n")));
    }
}
