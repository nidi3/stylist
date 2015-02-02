package guru.nidi.stylist.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import guru.nidi.stylist.rating.BatchCreator;
import guru.nidi.stylist.rating.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.util.Arrays.asList;

/**
 *
 */
@Service
public class Crawler {
    private final BatchCreator batchCreator;
    private final ObjectMapper mapper;
    private final Locator locator;
    private final Processor[] processors;

    @Autowired
    public Crawler(BatchCreator batchCreator, Locator locator, ObjectMapper mapper, Processor[] processors) {
        this.batchCreator = batchCreator;
        this.locator = locator;
        this.mapper = mapper;
        this.processors = processors;
    }

    public static void main(String[] args) throws CheckstyleException, IOException {
//        final File bd = new File("/Users/nidi/idea");
//        new GitProvider(bd).load("https://github.com/nidi3/raml-tester");
//        final InputStream config = Rater.class.getResourceAsStream("/sun_checks.xml");
//        try (final CheckstyleProcessor csp = new CheckstyleProcessor(config)) {
//            new Crawler(bd, mapper).apply(new Rater(csp));
//        }
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void apply() throws IOException {
        for (File projectDir : locator.projectsDir().listFiles(f -> f.isDirectory())) {
            final double rating = rate(projectDir);
            try (final FileOutputStream out = new FileOutputStream(locator.ratingByDir(projectDir))) {
                mapper.writeValue(out, rating);
            }
            try (final FileOutputStream out = new FileOutputStream(locator.batchByDir(projectDir))) {
                batchCreator.createBatch(rating, out);
            }
        }
    }

    private double rate(File basedir) throws IOException {
        double ratings = 0;
        for (Processor processor : processors) {
            ratings += processor.process(basedir, asList("/test/", "/target/"));
        }
        return 1 / (1 + 100 * ratings / processors.length);
    }

    public Status getStatus(String url) {
        final File project = locator.projectByUrl(url);
        if (!project.exists()) {
            return Status.NO_PROJECT;
        }
        final File rating = locator.ratingByDir(project);
        if (!rating.exists()) {
            return Status.NO_RATING;
        }
        return Status.COMPLETE;
    }

    public File getBatch(String url) throws IOException {
        final File batch = locator.batchByDir(locator.projectByUrl(url));
        if (!batch.exists()) {
            try (final FileOutputStream out = new FileOutputStream(batch)) {
                batchCreator.createInProgress(out);
            }
        }
        return batch;
    }

    public enum Status {
        NO_PROJECT, NO_RATING, COMPLETE
    }
}
