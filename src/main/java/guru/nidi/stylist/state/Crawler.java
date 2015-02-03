package guru.nidi.stylist.state;

import guru.nidi.stylist.rating.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

import static java.util.Arrays.asList;

/**
 *
 */
@Service
public class Crawler {
    private final Processor[] processors;

    @Autowired
    public Crawler(Processor[] processors) {
        this.processors = processors;
    }

    public void apply(Project project) {
        for (Processor processor : processors) {
            project.setRating(processor.calcRating(new File(project.getLocation()), asList("/test/", "/target/")));
        }
    }
}
