package guru.nidi.stylist.rating;

import java.io.File;
import java.util.List;

/**
 *
 */
public interface Processor {
    double process(File basedir, List<String> excludes);
}
