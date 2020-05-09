package eval.code.quality.utils;

import java.util.Objects;

public class StringError extends Error {
    public final String description;

    public StringError(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringError that = (StringError) o;
        return Objects.equals(description, that.description);
    }

    @Override
    public String toString() {
        return description;
    }
}
