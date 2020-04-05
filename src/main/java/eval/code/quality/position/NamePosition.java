package eval.code.quality.position;

public class NamePosition {
    public final String name;
    public final Position position;

    public NamePosition(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    @Override
    public String toString() {
        return name + " " + position;
    }
}
