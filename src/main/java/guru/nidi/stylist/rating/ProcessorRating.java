package guru.nidi.stylist.rating;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProcessorRating {
    private final List<FileRating> fileRatings = new ArrayList<>();

    public void addFileRating(FileRating fileRating) {
        fileRatings.add(fileRating);
    }

    public Double severityPerByte() {
        if (fileRatings.isEmpty()) {
            return null;
        }
        final double sum = fileRatings.stream()
                .map(FileRating::severityPerByte)
                .reduce((accu, severity) -> accu + severity)
                .orElse(0D);
        return sum / fileRatings.size();
    }
}
