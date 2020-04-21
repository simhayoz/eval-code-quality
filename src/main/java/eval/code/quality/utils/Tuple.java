package eval.code.quality.utils;

import java.util.Objects;

/**
 * Represents an immutable tuple.
 *
 * @param <L> the type of the first element
 * @param <R> the type of the second element
 */
public class Tuple<L, R> {
    public final L _1;
    public final R _2;

    public Tuple(L _1, R _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(_1, tuple._1) &&
                Objects.equals(_2, tuple._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "(" + _1 + "," + _2 + ")";
    }
}
