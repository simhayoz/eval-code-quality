package eval.code.quality.position;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NamePositionTest {

    @Test void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new NamePosition(null, new SinglePosition(0)));
        assertThrows(IllegalArgumentException.class, () -> new NamePosition("name", null));
    }

    @Test void equalsWithOtherNamePosition() {
        NamePosition namePosition = new NamePosition("name", new SinglePosition(2, 2));
        NamePosition otherNamePosition = new NamePosition("name", new SinglePosition(2, 2));
        NamePosition notEqualNamePosition = new NamePosition("name", new SinglePosition(3, 4));
        NamePosition notEqualNamePosition2 = new NamePosition("notSameName", new SinglePosition(3, 4));
        assertEquals(namePosition, namePosition);
        assertEquals(namePosition, otherNamePosition);
        assertNotEquals(namePosition, null);
        assertNotEquals(namePosition, new SinglePosition(2, 2));
        assertNotEquals(namePosition, notEqualNamePosition);
        assertNotEquals(namePosition, notEqualNamePosition2);
    }

    @Test void toStringWorksForSimplePosition() {
        NamePosition namePosition = new NamePosition("name", new SinglePosition(2, 2));
        assertEquals("name (line 2,col 2)", namePosition.toString());
    }
}
