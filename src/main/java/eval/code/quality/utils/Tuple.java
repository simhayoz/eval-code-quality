package eval.code.quality.utils;

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
}
