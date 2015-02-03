package guru.nidi.stylist.rating;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FileRating {
    private final String file;
    private final List<Issue> issues;

    public FileRating(String file) {
        this(file, new ArrayList<>());
    }

    @JsonCreator
    private FileRating(@JsonProperty("file") String file, @JsonProperty("issues") List<Issue> issues) {
        this.file = file;
        this.issues = issues;
    }

    public void addProblem(double severity, String description) {
        issues.add(new Issue(severity, description));
    }

    public double getSeverity() {
        return issues.stream()
                .map(Issue::getSeverity)
                .reduce((acc, val) -> acc + val)
                .orElse(0D) / new File(file).length();
    }

    public String getFile() {
        return file;
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
