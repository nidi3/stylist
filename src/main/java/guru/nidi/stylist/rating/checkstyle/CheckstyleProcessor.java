package guru.nidi.stylist.rating.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import guru.nidi.stylist.rating.FileLister;
import guru.nidi.stylist.rating.Processor;
import guru.nidi.stylist.rating.Rating;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 */
public class CheckstyleProcessor implements Processor {
    private final Checker checker;
    private final Rating rating;

    public CheckstyleProcessor(InputStream config) throws CheckstyleException {
        rating = new Rating();
        checker = new Checker();
        checker.setModuleClassLoader(getClass().getClassLoader());
        checker.configure(ConfigurationLoader.loadConfiguration(
                new InputSource(config),
                new PropertiesExpander(new Properties()), true));
        checker.addListener(new CheckstyleRater(rating));
    }

    @Override
    public Rating process(File basedir) {
        checker.process(new FileLister(basedir).list(".java"));
        checker.destroy();
        return rating;
    }

}
