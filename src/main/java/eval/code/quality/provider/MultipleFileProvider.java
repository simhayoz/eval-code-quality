package eval.code.quality.provider;

import eval.code.quality.utils.FileToString;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class MultipleFileProvider extends MultipleStringProvider {

    public MultipleFileProvider(List<File> files) {
        super(files.stream().map(f -> {
            try {
                return FileToString.fromFile(f);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File not found: " + e);
            }
        }).collect(Collectors.toList()));
    }
}
