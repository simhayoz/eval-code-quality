package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;

/**
 * Decorator for {@code Position} to add name of the {@code ContentProvider}.
 */
public class NamePosition extends Position {
    public final String name;
    public final Position position;

    /**
     * Create a new {@code NamePosition}.
     *
     * @param name     the name of the position
     * @param position the position
     */
    public NamePosition(String name, Position position) {
        Preconditions.checkArg(name != null, "The name cannot be null");
        Preconditions.checkArg(position != null, "The position cannot be null");
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
    public String toString() {
        return name + " " + position;
    }
}
