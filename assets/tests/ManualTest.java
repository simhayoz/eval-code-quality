import java.util.HashMap;
import java.util.Map;
 import java.util.Set;

public class Details {
    private int test;
    private String test2;
    public String test3;

    /**
     * This is a javadoc comment.
     *
     * @param str a string
     */
    public void countDupChars(String str) {


        //Create a HashMap
        Map<Character, Integer> map = new HashMap<Character, Integer>();

        //Convert the String to char array
        char[] chars = str.toCharArray();

        /* logic: char are inserted as keys and their count
         * as values. If map contains the char already then
         * increase the value by 1
         */
        for (Character ch : chars) {
            if (map.containsKey(ch)) {
                map.put(ch, map.get(ch) + 1);
            } else {
                map.put(ch, 1);
            }
        }

        {
            test();
        }

        //Obtaining set of keys
        Set<Character> keys = map.keySet();

        /* Display count of chars if it is
         * greater than 1. All duplicate chars would be
         * having value greater than 1.
         */
        for (Character ch : keys) {
            if (map.get(ch) > 1) {
                System.out.println("Char " + ch + " " + map.get(ch));
            }
        }
    }

     public static void main(String a[]) {
        Details obj = new Details();
        System.out.println("String: BeginnersBook.com");
        System.out.println("-------------------------");
        obj.countDupChars("BeginnersBook.com");

        System.out.println("\nString: ChaitanyaSingh");
          System.out.println("-------------------------");
        obj.countDupChars("ChaitanyaSingh");

        if(true)
            test();
        else {
            test2();
        }

        if(true) {
            test3();
        } else
            test4();


        System.out.println("\nString: #@$@!#$%!!%@");
        System.out.println("-------------------------");
        obj.countDupChars("#@$@!#$%!!%@");
    }

    public class Test {
        public static boolean test() {
            if(true)
                   return true;
            return false;
        }

        public void doNothing() {
                test();
                test2();
        }

        public void do_nothing() {

        }
    }

    public enum NewTest {
        TEST,
        NEW_TEST
    }
}