package eval.code.quality.utils;

import eval.code.quality.checks.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.DirectoryProvider;
import eval.code.quality.provider.FileProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.utils.parameters.*;
import org.apache.commons.cli.*;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ArgParser {

    private static final ArgParser instance = new ArgParser();
    public final Map<String, Class<? extends Check>> allPossibleCheck = new HashMap<>();

    private final Options options = new Options();
    public final HelpFormatter formatter = new HelpFormatter();

    private ArgParser() {
        setAllPossibleCheck();
        setOptions();
    }

    public static ArgParser getInstance() {
        return instance;
    }

    public TestRunner parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if(cmd.hasOption("help")) {
                formatter.printHelp("run", options);
                System.exit(0);
            } else if(cmd.hasOption("help-json")) {
                RunParameter.printJsonDescription();
                System.exit(0);
            } else {
                JSONObject jsonObject = new JSONObject();
                if(cmd.hasOption("json")) {
                    if(cmd.getOptionValue("json").substring(cmd.getOptionValue("json").lastIndexOf(".") + 1).trim().toLowerCase().equals("json")) {
                        String content;
                        try(Scanner scanner = new Scanner(new FileInputStream(cmd.getOptionValue("json"))).useDelimiter("\\A")) {
                            content =  scanner.hasNext() ? scanner.next() : "";
                        }
                        jsonObject = new JSONObject(content);
                    } else {
                        System.out.println("Can not parse input config to another format than json");
                        formatter.printHelp("run", options);
                        System.exit(1);
                    }
                }
                String name = new NameParameter().getValue(cmd, jsonObject);
                boolean shouldPrintToSysOut = cmd.hasOption("sysout") || (jsonObject.has("sysout") && jsonObject.getBoolean("sysout"));
                List<ContentProvider> contentProviders = new ArrayList<>();
                contentProviders.addAll(new DirectoryParameter().getValue(cmd, jsonObject));
                contentProviders.addAll(new FileParameter().getValue(cmd, jsonObject));
                MultipleContentProvider multipleContentProvider = new MultipleContentProvider(contentProviders);
                List<Check> checks = new ArrayList<>(new CheckParameter(multipleContentProvider).getValue(cmd, jsonObject));
                checks.addAll(new DesignPatternParameter(multipleContentProvider).getValue(cmd, jsonObject));
                File output = new OutputParameter().getValue(cmd, jsonObject);
                return new TestRunner(name, shouldPrintToSysOut, multipleContentProvider, output, checks);
            }
        } catch (ParseException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("run", options);
            System.exit(1);
        }
        return null;
    }

    private void setAllPossibleCheck() {
        allPossibleCheck.put("blank lines", BlankLines.class);
        allPossibleCheck.put("braces", Braces.class);
        allPossibleCheck.put("indentation", Indentation.class);
        allPossibleCheck.put("naming", Naming.class);
    }

    private void setOptions() {
        Option help = new Option("h", "help", false, "Display this help message and exit");
        options.addOption(help);

        Option helpJson = new Option("hjson", "help-json", false, "Display help message for JSON config and exit");
        options.addOption(helpJson);

        Option sysOut = new Option("s", "sysout", false, "If present will print report to the terminal");
        options.addOption(sysOut);

        Option output = new Option("o", "output", true, "Specify output xml file");
        options.addOption(output);

        Option directory = new Option("d", "directory", true, "Path to directory containing Java file to analyze");
        directory.setArgs(Option.UNLIMITED_VALUES);
        directory.setValueSeparator(',');
        options.addOption(directory);

        Option file = new Option("f", "file", true, "Path to a Java file to analyze");
        file.setArgs(Option.UNLIMITED_VALUES);
        file.setValueSeparator(',');
        options.addOption(file);

        Option testSuiteName = new Option("n", "name", true, "Set the name of the current test suite");
        options.addOption(testSuiteName);

        Option checkOption = new Option("c", "check", true,
                "Set the checks to do from the following possibilities: "
                        + allPossibleCheck.keySet().stream().map(c -> "'" + c + "'").collect(Collectors.joining(", "))
                        + ", if not set or contains the value 'all' will run all the checks");
        checkOption.setArgs(Option.UNLIMITED_VALUES);
        checkOption.setValueSeparator(',');
        options.addOption(checkOption);

        Option jsonFile = new Option("j", "json", true, "Set the json config file (warning terminal arguments will override config file if defined in both)");
        options.addOption(jsonFile);
    }

    public void printHelp() {
        formatter.printHelp("run", options);
    }

}
