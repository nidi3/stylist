package guru.nidi.stylist.state;

import java.io.File;

/**
 *
 */
public class Locator {
    private final File projectsDir;

    public Locator(File projectsDir) {
        this.projectsDir = projectsDir;
    }

    private String normalized(String url) {
        StringBuilder s = new StringBuilder(url);
        int pos = url.indexOf("://");
        if (pos >= 0) {
            s.delete(0, pos + 3);
        }
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                s.setCharAt(i, '-');
            }
        }
        return s.toString();
    }

    public File projectByUrl(String url) {
        return new File(projectsDir, normalized(url));
    }

    public File lockByUrl(String url) {
        return new File(projectsDir, "." + normalized(url));
    }

    public File projectsDir() {
        return projectsDir;
    }

    public File ratingByDir(File dir) {
        return new File("target/rating-" + dir.getName() + ".txt");
    }
    public File batchByDir(File dir) {
        return new File("target/batch-" + dir.getName() + ".svg");
    }
}
