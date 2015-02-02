package guru.nidi.stylist.rating;

import java.io.File;
import java.util.List;

/**
 *
 */
public interface Processor {
    Double calcSeverity(File basedir, List<String> excludes);
}
