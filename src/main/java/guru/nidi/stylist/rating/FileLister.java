package guru.nidi.stylist.rating;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FileLister {
    private final File basedir;
    private final List<String> excludes;

    public FileLister(File basedir, List<String> excludes) {
        this.basedir = basedir;
        this.excludes = excludes;
    }

    public List<File> list(String suffix) {
        final List<File> list = new ArrayList<>();
        list(basedir, file ->
                excludes.stream().allMatch(exclude -> !file.getAbsolutePath().contains(exclude)) &&
                        file.getName().endsWith(suffix), list);
        return list;
    }

    private void list(File basedir, FileFilter filter, List<File> list) {
        final File[] files = basedir.listFiles(filter);
        if (files != null) {
            for (File file : files) {
                list.add(file);
            }
        }

        final File[] dirs = basedir.listFiles(file -> file.isDirectory());
        if (dirs != null) {
            for (File sub : dirs) {
                list(sub, filter, list);
            }
        }
    }
}
