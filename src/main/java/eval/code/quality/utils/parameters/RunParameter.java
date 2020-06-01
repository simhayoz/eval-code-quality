package eval.code.quality.utils.parameters;

import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class RunParameter<T> {

    public abstract T getValue(CommandLine cmd, JSONObject jsonObject);

    protected boolean isNonNull(String[] strings) {
        return strings != null && strings.length > 0;
    }

    protected void addDefinedBoth(String content) {
        addWarning(content + " defined in JSON and command line, ignoring JSON value");
    }

    protected void addWarning(String content) {
        System.out.println("Warning: " + content);
    }

    protected void throwErrorJSON(String content) {
        System.out.println("Error: " + content);
        printJsonDescription();
        System.exit(1);
    }

    protected void printJsonDescription() {
        try(Scanner scanner = new Scanner(new FileInputStream("src/main/resources/JSONDescription.txt")).useDelimiter("\\A")) {
            System.out.println("JSON file description:");
            System.out.println(scanner.hasNext() ? scanner.next() : "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
