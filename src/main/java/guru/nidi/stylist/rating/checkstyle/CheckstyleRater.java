package guru.nidi.stylist.rating.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import guru.nidi.stylist.rating.FileRating;
import guru.nidi.stylist.rating.Rating;

/**
 *
 */
public class CheckstyleRater implements AuditListener {
    private final Rating rating;
    private FileRating fileRating;

    public CheckstyleRater(Rating rating) {
        this.rating = rating;
    }

    @Override
    public void auditStarted(AuditEvent evt) {
    }

    @Override
    public void auditFinished(AuditEvent evt) {
    }

    @Override
    public void fileStarted(AuditEvent evt) {
        fileRating = new FileRating(evt.getFileName());
    }

    @Override
    public void fileFinished(AuditEvent evt) {
        rating.addFileRating(fileRating);
    }

    @Override
    public void addError(AuditEvent evt) {
        fileRating.addProblem(severity(evt.getSeverityLevel()));
    }

    private double severity(SeverityLevel level) {
        switch (level) {
            case IGNORE:
                return .1;
            case INFO:
                return .2;
            case WARNING:
                return .7;
            case ERROR:
                return 1;
        }
        throw new AssertionError();
    }

    @Override
    public void addException(AuditEvent evt, Throwable throwable) {
//TODO log
    }
}
