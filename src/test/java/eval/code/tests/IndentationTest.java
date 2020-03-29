package eval.code.tests;

import org.junit.jupiter.api.Test;

import eval.code.tools.ProcessCU;
import eval.code.tools.pos.Position;
import eval.code.tools.pos.ReportPosition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.Matchers;

class IndentationTest {

    @Test
    void emptyCUReportNoError() {
        Report r = new Indentation(ProcessCU.fromString("").getCU(), "").runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test
    void testForSimpleWhileForIfWorks() {
        String[] blocks_to_test = { "for(int i=0; i < 2; ++i) {\n    return true;\n}",
                "if(true) {\n    return true;\n} else {\n    return false;\n}", "if(true)\n    return true;",
                "while(true) {\n    System.out.println();\n    return true;\n}",
                "{\n    System.out.println();\n    return true;\n}",
                "int i = 0;\nswitch (i) {\n    case 0:\n        return true;\n        break;\n    case 1:\n        return false;\n        break;\n    default:\n        return false;\n        break;\n}" };
        for (String s : blocks_to_test) {
            String wrapper = wrap(s);
            Report r = new Indentation(ProcessCU.fromString(wrapper).getCU(), wrapper).runTest();
            assertThat(r.getWarnings(), is(empty()));
            assertThat(r.getErrors(), is(empty()));
        }
    }

    @Test
    void testForSimpleWhileForIfFailsWhenMisaligned() {
        Map<String, Position> blocks_to_test = new HashMap<>();
        blocks_to_test.put("for(int i=0; i < 2; ++i) {\n  return true;\n}", Position.setPos(4, 10));
        blocks_to_test.put("if(true) {\n    return true;\n} else {\n     return false;\n}", Position.setPos(6, 13));
        blocks_to_test.put("if(true)\n     return true;", Position.setPos(4, 13));
        blocks_to_test.put("while(true) {\n    System.out.println();\n    System.out.println();\n      return true;\n}",
                Position.setPos(6, 14));
        blocks_to_test.put("{\n    System.out.println();\n    System.out.println();\n     return true;\n}",
                Position.setPos(6, 13));
        blocks_to_test.put(
                "int i = 0;\nswitch (i) {\n    case 0:\n    return true;\n        System.out.println();\n        break;\n    case 1:\n        return false;\n        break;\n    default:\n        return false;\n        break;\n}",
                Position.setPos(6, 12));
        for (Entry<String, Position> s : blocks_to_test.entrySet()) {
            String wrapper = wrap(s.getKey());
            Report r = new Indentation(ProcessCU.fromString(wrapper).getCU(), wrapper).runTest();
            assertThat(wrapper, r.getWarnings(), is(empty()));
            assertThat(wrapper, r.getErrors(), Matchers
                    .<Collection<ReportPosition>>allOf(hasItem(is(ReportPosition.at(s.getValue()))), hasSize(1)));
        }
    }

    @Test
    void testForTryCatchBlocksWorksForMultipleCatch() {
        String[] blocks_to_test = {
                "try {\n    System.out.println();\n    return true;\n} catch (Exception e) {\n    return false;\n}",
                "try {\n    System.out.println();\n    return true;\n} catch (Exception e) {\n    return false;\n} catch (NullPointerException n) {\n    return false;\n}",
                "try {\n    System.out.println();\n    return true;\n} \ncatch (Exception e) {\n    return false;\n} \ncatch (NullPointerException n) {\n    return false;\n}" };
        for (String s : blocks_to_test) {
            String wrapper = wrap(s);
            Report r = new Indentation(ProcessCU.fromString(wrapper).getCU(), wrapper).runTest();
            assertThat(wrapper, r.getWarnings(), is(empty()));
            assertThat(wrapper, r.getErrors(), is(empty()));
        }
    }

    @Test
    void testForTryCatchBlocksNotAlignedFails() {
        Map<String, Position> blocks_to_test = new HashMap<>();
        blocks_to_test.put(
                "try {\n     System.out.println();\n    System.out.println();\n    return true;\n} catch (Exception e) {\n    return false;\n}",
                Position.setPos(4, 13));
        blocks_to_test.put(
                "try {\n    System.out.println();\n    return true;\n} \ncatch (Exception e) {\n    return false;\n} \n  catch (NullPointerException n) {\n    return false;\n}",
                Position.setPos(10, 10));
        for (Entry<String, Position> s : blocks_to_test.entrySet()) {
            String wrapper = wrap(s.getKey());
            Report r = new Indentation(ProcessCU.fromString(wrapper).getCU(), wrapper).runTest();
            assertThat(wrapper, r.getWarnings(), is(empty()));
            assertThat(wrapper, r.getErrors(), Matchers
                    .<Collection<ReportPosition>>allOf(hasItem(is(ReportPosition.at(s.getValue()))), hasSize(1)));
        }
    }

    @Test
    void twoAlignedButDifferentBlocksFails() {
        String b1 = "try {\n    System.out.println();\n    return true;\n} catch (Exception e) {\n    return false;\n}";
        String b2 = "while(true) {\n        System.out.println();\n        return true;\n}";
        String wrapper = wrap(new String[] { b1, b2 });
        Report r = new Indentation(ProcessCU.fromString(wrapper).getCU(), wrapper).runTest();
        assertThat(wrapper, r.getWarnings(), is(empty()));
        assertThat(wrapper, r.getErrors(),
                Matchers.<Collection<ReportPosition>>allOf(
                        hasItem(is(ReportPosition.at(
                                Position.setRangeOrSinglePos(Position.setPos(11, 16), Position.setPos(12, 16))))),
                        hasSize(1)));
    }

    private String wrap(String s) {
        String[] arr = { s };
        return wrap(arr);
    }

    private String wrap(String[] s) {
        StringBuilder blocks = new StringBuilder();
        for (String b : s) {
            blocks.append(b.indent(8) + "\n");
        }
        return "public class Test {\n    public static boolean test() {\n" + blocks.toString() + "\n    }\n}";
    }
}