package guru.nidi.stylist.rating.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import guru.nidi.stylist.rating.FileLister;
import guru.nidi.stylist.rating.Processor;
import guru.nidi.stylist.rating.ProcessorRating;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class CheckstyleProcessor implements Processor, AutoCloseable {
    private final Checker checker;

    public CheckstyleProcessor(InputStream config) throws CheckstyleException {
        checker = new Checker();
        checker.setModuleClassLoader(getClass().getClassLoader());
        checker.configure(ConfigurationLoader.loadConfiguration(
                new InputSource(config),
                new PropertiesExpander(new Properties()), true));
    }

    @Override
    public ProcessorRating calcRating(File basedir, List<String> excludes) {
        final ProcessorRating processorRating = new ProcessorRating("Checkstyle");
        final CheckstyleRater listener = new CheckstyleRater(processorRating);
        checker.addListener(listener);
        checker.process(new FileLister(basedir, excludes).list(".java"));
        checker.removeListener(listener);
        return processorRating;
    }

    @Override
    public void close() {
        checker.destroy();
    }
}
