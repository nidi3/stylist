package guru.nidi.stylist.rating;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class Issue {
    private final double severity;
    private final String description;

    public Issue(@JsonProperty("severity") double severity, @JsonProperty("description") String description) {
        this.severity = severity;
        this.description = description;
    }

    public double getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }
}
