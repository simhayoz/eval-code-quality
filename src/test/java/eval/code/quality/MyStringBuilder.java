package eval.code.quality;

/**
 * Helper class for creating string for test.
 */
public class MyStringBuilder {
    StringBuilder sb = new StringBuilder();

    public MyStringBuilder add(String s) {
        sb.append(s);
        return this;
    }

    public MyStringBuilder addLn(String s) {
        return addLn(s, 0);
    }

    public MyStringBuilder addLn(String s, int indentation) {
        sb.append(s.indent(indentation));
        return this;
    }

    public MyStringBuilder addBlankLine() {
        sb.append(System.lineSeparator());
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
