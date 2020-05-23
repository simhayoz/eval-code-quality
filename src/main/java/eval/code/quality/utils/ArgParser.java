package eval.code.quality.utils;

import eval.code.quality.checks.*;
import eval.code.quality.provider.DirectoryProvider;
import eval.code.quality.provider.FileProvider;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ArgParser {

    private static final ArgParser instance = new ArgParser();
    public final Map<String, Class<? extends Check>> allPossibleCheck = new HashMap<>();

    private final Options options = new Options();

    private ArgParser() {
        setAllPossibleCheck();
        setOptions();
    }

    public static ArgParser getInstance() {
        return instance;
    }

    public TestRunner parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if(cmd.hasOption("help")) {
                formatter.printHelp("run", options);
                System.exit(0);
            }
            TestRunner.Builder builder = new TestRunner.Builder(cmd.getOptionValue("name"), cmd.hasOption("sysout"));
            if(cmd.hasOption("json")) {
                if(checkHasExt(cmd.getOptionValue("json"), "json")) {
                    builder = JSONParser.getInstance().parse(new FileInputStream(cmd.getOptionValue("json")));
                    if(builder.getName() != null && cmd.hasOption("name")) {
                        System.out.println("Name was also defined by command line ignoring config file name");
                        builder.setName(cmd.getOptionValue("name"));
                    } else {
                        if(builder.getName() == null && !cmd.hasOption("name")) {
                            System.out.println("Name should be defined at least once");
                            formatter.printHelp("run", options);
                            System.exit(1);
                        }
                    }
                } else {
                    System.out.println("Can not get config from another format than json");
                    formatter.printHelp("run", options);
                    System.exit(1);
                }
            }
            if(cmd.hasOption("output")) {
                if(checkHasExt(cmd.getOptionValue("output"), "xml")) {
                    if(builder.getXmlOutput() != null && !builder.getXmlOutput().getName().equals(cmd.getOptionValue("output"))) {
                        System.out.println("XML output define twice, ignoring config file");
                    }
                    builder.setXmlOutput(new File(cmd.getOptionValue("output")));
                } else {
                    System.out.println("Can not parse to another format than xml");
                    System.exit(1);
                }
            }
            if(isNonNull(cmd.getOptionValues("directory"))) {
                Arrays.stream(cmd.getOptionValues("directory"))
                        .filter(dir -> !dir.trim().equals("irectory"))
                        .map(DirectoryProvider::new)
                        .forEach(builder::addContentProviders);
            }
            if(isNonNull(cmd.getOptionValues("file"))) {
                Arrays.stream(cmd.getOptionValues("file"))
                        .map(f -> new FileProvider(new File(f)))
                        .forEach(builder::addContentProviders);
            }
            Set<Class<? extends Check>> set = new HashSet<>();
            if(isNonNull(cmd.getOptionValues("check"))) {
                Arrays.stream(cmd.getOptionValues("check"))
                        .forEach(check -> {
                            if(allPossibleCheck.containsKey(check.trim().toLowerCase())) {
                                set.add(allPossibleCheck.get(check.trim().toLowerCase()));
                            } else {
                                System.out.println("Unknown check: \"" + check + "\" ignoring");
                            }
                        });
                if(Arrays.stream(cmd.getOptionValues("check")).anyMatch(e -> e.trim().toLowerCase().equals("all"))) {
                    allPossibleCheck.forEach((k, v) -> set.add(v));
                }
                if(!builder.getCheck().isEmpty() && (!set.containsAll(builder.getCheck()) || !builder.getCheck().containsAll(set))) {
                    builder.clearCheck();
                    System.out.println("Checks where defined differently in json config and in command line argument, ignoring json config");
                }
                for(Class<? extends Check> c : set) {
                    builder.addCheck(c);
                }
            }
            return builder.build();
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

        Option checkToDo = new Option("c", "check", true,
                "Set the checks to do from the following possibilities: "
                        + allPossibleCheck.keySet().stream().map(c -> "'" + c + "'").collect(Collectors.joining(", "))
                        + ", if not set or contains the value 'all' will run all the checks");
        checkToDo.setArgs(Option.UNLIMITED_VALUES);
        checkToDo.setValueSeparator(',');
        options.addOption(checkToDo);

        Option jsonFile = new Option("j", "json", true, "Set the json config file (warning terminal arguments will override config file if defined in both)");
        options.addOption(jsonFile);

        // TODO add option for design pattern
    }

    private boolean isNonNull(String[] strings) {
        return strings != null && strings.length > 0;
    }

    public boolean checkHasExt(String file, String extension) {
        return file.substring(file.lastIndexOf(".") + 1).trim().toLowerCase().equals(extension);
    }

}
