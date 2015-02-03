package guru.nidi.stylist.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.nidi.stylist.rating.ProcessorRating;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public class Project {
    private final String name;
    private final String location;
    private final String origin;
    private final List<ProcessorRating> ratings;
    private Double severity;

    public Project(String name, String location, String origin) {
        this(name, location, origin, new ArrayList<>(), null);
    }

    @JsonCreator
    private Project(@JsonProperty("name") String name, @JsonProperty("location") String location,
                    @JsonProperty("origin") String origin, @JsonProperty("ratings") List<ProcessorRating> ratings,
                    @JsonProperty("severity") Double severity) {
        this.name = name;
        this.origin = origin;
        this.location =location;
        this.ratings = ratings == null ? new ArrayList<>() : ratings;
        this.severity = severity;
    }

    public void setRating(ProcessorRating rating) {
        final ListIterator<ProcessorRating> iter = ratings.listIterator();
        while (iter.hasNext()) {
            final ProcessorRating r = iter.next();
            if (r.getProcessor().equals(rating.getProcessor())) {
                iter.set(rating);
                return;
            }
        }
        ratings.add(rating);
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getOrigin() {
        return origin;
    }

    public Double getSeverity() {
        if (severity == null) {
            calcSeverity();
        }
        return severity;
    }

    public List<ProcessorRating> getRatings() {
        return ratings;
    }

    public void calcSeverity() {
        double severitySum = 0;
        int count = 0;
        for (ProcessorRating rating : ratings) {
            if (rating.getSeverity() != null) {
                severitySum += rating.getSeverity();
                count++;
            }
        }
        severity = count == 0 ? null : 1 / (1 + 100 * severitySum / count);
    }
}
