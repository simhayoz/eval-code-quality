package example;

public class CannotInferExample {
    private int withCamelCase;
    private int otherCamelCase;
    private String with_underscore;
    private String other_underscore;
    private boolean WITH_UPPER;
    private boolean OTHER_UPPER;

    public static void forIndentation() {
            System.out.println("is more indented than previous block");
    }
}