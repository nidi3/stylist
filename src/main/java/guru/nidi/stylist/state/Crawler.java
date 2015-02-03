package guru.nidi.stylist.state;

import guru.nidi.stylist.rating.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.util.Arrays.asList;

/**
 *
 */
@Service
public class Crawler {
    private final Database database;
    private final Processor[] processors;

    @Autowired
    public Crawler(Database database, Processor[] processors) {
        this.database = database;
        this.processors = processors;
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void applyAll() throws IOException {
        for (Project project : database.getProjects()) {
            apply(project);
        }
    }

    public void apply(Project project) throws IOException {
        for (Processor processor : processors) {
            project.setRating(processor.calcRating(project.getDir(), asList("/test/", "/target/")));
        }
        database.update(project);
    }
}
