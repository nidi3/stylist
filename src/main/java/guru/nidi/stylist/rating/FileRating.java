package guru.nidi.stylist.rating;

import java.io.File;

/**
 *
 */
public class FileRating {
    private final String file;
    private double severitySum;

    public FileRating(String file) {
        this.file = file;
    }

    public void addProblem(double severity) {
        severitySum += severity;
    }

    public double severityPerByte() {
        return severitySum / new File(file).length();
    }
}
