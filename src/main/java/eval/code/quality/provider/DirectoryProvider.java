package eval.code.quality.provider;

import eval.code.quality.utils.Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Content provider represented by a directory and every java file inside this directory.
 */
public class DirectoryProvider extends MultipleContentProvider {

    private final String path;

    /**
     * Create a new {@code DirectoryProvider} from the path to the directory.
     *
     * @param path the path to the directory
     */
    public DirectoryProvider(String path) {
        super(getListOfFileProvider(path));
        this.path = path;
    }

    private static List<ContentProvider> getListOfFileProvider(String path) {
        Preconditions.checkArg(path != null, "Path to directory can not be null");
        File directory = new File(path);
        Preconditions.checkArg(directory.exists(), "Path to directory does not exist");
        Preconditions.checkArg(directory.isDirectory(), "Path does not lead to a directory");
        List<File> acc = new ArrayList<>();
        findFileWithExtension(directory, acc);
        return acc.stream().map(FileProvider::new).collect(Collectors.toList());
    }

    private static void findFileWithExtension(File directory, List<File> acc) {
        File[] tempFiles = directory.listFiles(file -> {
            if (file.isDirectory()) {
                findFileWithExtension(file, acc);
            }
            return file.getName().toLowerCase().endsWith(".java");
        });
        if (tempFiles != null) {
            acc.addAll(Arrays.asList(tempFiles));
        }
    }

    @Override
    public String getName() {
        return "directory provider: '" + path + "'";
    }
}
