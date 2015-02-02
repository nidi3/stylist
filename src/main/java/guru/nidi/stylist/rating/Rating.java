package guru.nidi.stylist.rating;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Rating {
    private final List<FileRating> fileRatings = new ArrayList<>();

    public void addFileRating(FileRating fileRating) {
        fileRatings.add(fileRating);
    }

    public double rating() {
        final double sum = fileRatings.stream()
                .map(FileRating::severityPerByte)
                .reduce((accu, severity) -> accu + severity)
                .orElse(0D);
        return 1 / (1 + 100 * sum / fileRatings.size());
    }
}
