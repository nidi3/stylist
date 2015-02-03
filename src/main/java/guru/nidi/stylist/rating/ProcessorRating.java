package guru.nidi.stylist.rating;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProcessorRating {
    private final String processor;
    private final List<FileRating> ratings;

    public ProcessorRating(String processor) {
        this(processor, new ArrayList<>());
    }

    @JsonCreator
    private ProcessorRating(@JsonProperty("processor") String processor, @JsonProperty("ratings") List<FileRating> ratings) {
        this.processor = processor;
        this.ratings = ratings;
    }

    public void addFileRating(FileRating fileRating) {
        ratings.add(fileRating);
    }

    public Double getSeverity() {
        if (ratings.isEmpty()) {
            return null;
        }
        final double sum = ratings.stream()
                .map(FileRating::getSeverity)
                .reduce((accu, severity) -> accu + severity)
                .orElse(0D);
        return sum / ratings.size();
    }

    public List<FileRating> getRatings() {
        return ratings;
    }

    public String getProcessor() {
        return processor;
    }
}
