import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class Stack {

    public static final Stack INSTANCE = new Stack();
    private final List<String> list;

    private Stack() {
        list = new ArrayList<>();
    }

    public static void main(String[] args) {
        Stack stack = Stack.INSTANCE;
        stack.add("this")
                .add("is")
                .add("an example");
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }

    /**
     * Add to the stack.
     *
     * @param newString the string to add
     * @return this
     */
    public Stack add(String newString) {
        list.add(newString);
        return this;
    }

    /**
     * Pop the last String added in the list.
     *
     * @return the last String in the list
     */
    public String pop() {
        int size = list.size();
        if (size > 0) {
            String last = list.get(size - 1);
            list.remove(size - 1);
            return last;
        }
        throw new EmptyStackException();
    }
}