package eval.code.quality.position;

import java.util.Objects;

/**
 * Decorator for {@code Position} to add name of the {@code ContentProvider}.
 */
public class NamePosition extends Position {
    public final String name;
    public final Position position;

    public NamePosition(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamePosition that = (NamePosition) o;
        return this.name.equals(that.name) && this.position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position);
    }

    @Override
    public String toString() {
        return name + " " + position;
    }
}
