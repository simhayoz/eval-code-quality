package eval.code;

public class MySBuilder {
    StringBuilder sb = new StringBuilder();

    public MySBuilder add(String s) {
        sb.append(s);
        return this;
    }

    public MySBuilder addLn(String s) {
        return addLn(s, 0);
    }

    public MySBuilder addLn(String s, int indentation) {
        sb.append(s.indent(indentation));
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}