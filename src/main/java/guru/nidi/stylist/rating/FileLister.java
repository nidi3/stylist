package guru.nidi.stylist.rating;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FileLister {
    private final File basedir;

    public FileLister(File basedir) {
        this.basedir = basedir;
    }

    public List<File> list(String suffix) {
        final List<File> list = new ArrayList<>();
        list(basedir, (dir, name) -> name.endsWith(suffix), list);
        return list;
    }

    private void list(File basedir, FilenameFilter filter, List<File> list) {
        for (File file : basedir.listFiles(filter)) {
            list.add(file);
        }
        for (File sub : basedir.listFiles(file -> file.isDirectory())) {
            list(sub, filter, list);
        }
    }
}
