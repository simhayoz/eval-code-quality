package eval.code.quality.utils;

import eval.code.quality.checks.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.utils.parameters.*;
import org.apache.commons.cli.*;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Command line argument parser.
 */
public class ArgParser {

    private static final ArgParser instance = new ArgParser();
    private final Options options = new Options();
    private final HelpFormatter formatter = new HelpFormatter();

    protected ArgParser() {
        setOptions();
    }

    /**
     * Get the instance of {@code ArgParser}.
     *
     * @return the instance of {@code ArgParser}
     */
    public static ArgParser getInstance() {
        return instance;
    }

    /**
     * Parse the list of argument to a {@code TestRunner} for running the checks depending on the configuration parsed.
     *
     * @param args the list of argument
     * @return a {@code TestRunner} for running the checks depending on the configuration parsed
     */
    public TestRunner parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                getFormatter().printHelp("run", options);
                exitSystem(0);
            } else if (cmd.hasOption("help-json")) {
                printJSONHelp();
                exitSystem(0);
            } else {
                JSONObject jsonObject = getJSONParam(cmd);
                String name = NameParameter.getInstance().getValue(cmd, jsonObject);
                boolean shouldPrintToSysOut = cmd.hasOption("sysout") || (jsonObject.has("sysout") && jsonObject.getBoolean("sysout"));
                List<ContentProvider> contentProviders = new ArrayList<>();
                contentProviders.addAll(DirectoryParameter.getInstance().getValue(cmd, jsonObject));
                contentProviders.addAll(FileParameter.getInstance().getValue(cmd, jsonObject));
                MultipleContentProvider multipleContentProvider = new MultipleContentProvider(contentProviders);
                List<Check> checks = new ArrayList<>(new CheckParameter(multipleContentProvider).getValue(cmd, jsonObject));
                checks.addAll(new DesignPatternParameter(multipleContentProvider).getValue(cmd, jsonObject));
                File output = OutputParameter.getInstance().getValue(cmd, jsonObject);
                return new TestRunner(name, shouldPrintToSysOut, multipleContentProvider, output, checks);
            }
        } catch (ParseException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            getFormatter().printHelp("run", options);
            exitSystem(1);
        }
        return null;
    }

    /**
     * Print helper message for command line arguments.
     */
    public void printCmdHelp() {
        getFormatter().printHelp("run", options);
    }

    /**
     * Print helper message for json config file.
     */
    public void printJSONHelp() {
        try (Scanner scanner = new Scanner(new FileInputStream("src/main/resources/JSONDescription.txt")).useDelimiter("\\A")) {
            System.out.println("JSON helper description:");
            System.out.println(scanner.hasNext() ? scanner.next() : "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJSONParam(CommandLine cmd) throws FileNotFoundException {
        JSONObject jsonObject = new JSONObject();
        if (cmd.hasOption("json")) {
            if (cmd.getOptionValue("json").substring(cmd.getOptionValue("json").lastIndexOf(".") + 1).trim().toLowerCase().equals("json")) {
                String content;
                try (Scanner scanner = new Scanner(new FileInputStream(cmd.getOptionValue("json"))).useDelimiter("\\A")) {
                    content = scanner.hasNext() ? scanner.next() : "";
                }
                jsonObject = new JSONObject(content);
            } else {
                System.out.println("Can not parse input config to another format than json");
                getFormatter().printHelp("run", options);
                exitSystem(1);
            }
        }
        return jsonObject;
    }

    /**
     * Exit system with the result code.
     * @param code the result code
     */
    protected void exitSystem(int code) {
        System.exit(code);
    }

    /**
     * Get formatter for helper message pretty print.
     * @return formatter for helper message pretty print
     */
    protected HelpFormatter getFormatter() {
        return formatter;
    }

    protected void setOptions() {
        List<Option> optionList = new ArrayList<>();
        optionList.add(new Option("h", "help", false, "Display this help message and exit"));

        optionList.add(new Option("hjson", "help-json", false, "Display help message for JSON config and exit"));

        optionList.add(new Option("s", "sysout", false, "If present will print report to the terminal"));

        optionList.add(new Option("o", "output", true, "Specify output xml file"));

        Option directory = new Option("d", "directory", true, "Path to directory containing Java file to analyze");
        directory.setArgs(Option.UNLIMITED_VALUES);
        directory.setValueSeparator(',');
        optionList.add(directory);

        Option file = new Option("f", "file", true, "Path to a Java file to analyze");
        file.setArgs(Option.UNLIMITED_VALUES);
        file.setValueSeparator(',');
        optionList.add(file);

        optionList.add(new Option("n", "name", true, "Required: set the name of the current test suite"));

        Option checkOption = new Option("c", "check", true,
                "Set the checks to do from the following possibilities: "
                        + CheckParameter.allPossibleCheck.keySet().stream().map(c -> "'" + c + "'").collect(Collectors.joining(", "))
                        + ", if not set or contains the value 'all' will run all the checks");
        checkOption.setArgs(Option.UNLIMITED_VALUES);
        checkOption.setValueSeparator(',');
        optionList.add(checkOption);

        optionList.add(new Option("j", "json", true, "Set the json config file (warning terminal arguments will override config file if defined in both)"));

        optionList.forEach(options::addOption);
    }
}
