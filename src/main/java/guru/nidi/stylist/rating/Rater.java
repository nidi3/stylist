package guru.nidi.stylist.rating;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import guru.nidi.stylist.rating.checkstyle.CheckstyleProcessor;

import java.io.File;
import java.io.InputStream;

/**
 *
 */
public class Rater {
    public static void main(String[] args) throws CheckstyleException {
        final InputStream config = Rater.class.getResourceAsStream("/sun_checks.xml");
        final Rating rating = new CheckstyleProcessor(config).process(new File("src/main/java"));
        System.out.println(rating.rating());
    }
}
