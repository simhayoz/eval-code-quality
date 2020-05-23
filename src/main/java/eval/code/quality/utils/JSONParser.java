package eval.code.quality.utils;

import eval.code.quality.provider.DirectoryProvider;
import eval.code.quality.provider.FileProvider;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class JSONParser {

    private static final JSONParser instance = new JSONParser();

    public static JSONParser getInstance() {
        return instance;
    }

    public TestRunner.Builder parse(InputStream inputStream) {
        String content;
        try(Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            content =  scanner.hasNext() ? scanner.next() : "";
        }
        JSONObject obj = new JSONObject(content);
        TestRunner.Builder builder = new TestRunner.Builder(obj.has("name") ? obj.getString("name") : null, obj.has("sysout") && obj.getBoolean("sysout"));
        if(obj.has("directory")) {
            for(Object o : obj.getJSONArray("directory")) {
                builder.addContentProviders(new DirectoryProvider(o.toString()));
            }
        }
        if(obj.has("file")) {
            for(Object o : obj.getJSONArray("file")) {
                builder.addContentProviders(new FileProvider(new File(o.toString())));
            }
        }
        if(obj.has("check")) {
            for(Object o : obj.getJSONArray("check")) {
                if(o.toString().trim().toLowerCase().equals("all")) {
                    ArgParser.getInstance().allPossibleCheck.values().forEach(builder::addCheck);
                } else {
                    if(ArgParser.getInstance().allPossibleCheck.containsKey(o.toString())) {
                        builder.addCheck(ArgParser.getInstance().allPossibleCheck.get(o.toString()));
                    } else {
                        System.out.println("Unknown check: \"" + o.toString() + "\" ignoring");
                    }
                }
            }
        } else {
            ArgParser.getInstance().allPossibleCheck.values().forEach(builder::addCheck);
        }
        if(obj.has("output")) {
            if(ArgParser.getInstance().checkHasExt(obj.getString("output"), "xml")) {
                builder.setXmlOutput(new File(obj.getString("output")));
            } else {
                System.out.println("Can not parse to another format than xml");
                System.exit(1);
            }
        }
        return builder;
    }
}
