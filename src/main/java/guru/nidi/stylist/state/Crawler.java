package guru.nidi.stylist.state;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void apply() throws IOException {
        for (File projectDir : locator.projectsDir().listFiles(f -> f.isDirectory())) {
            final Double rating = severity(projectDir);
            try (final FileOutputStream out = new FileOutputStream(locator.ratingByDir(projectDir))) {
                mapper.writeValue(out, rating);
            }
            try (final FileOutputStream out = new FileOutputStream(locator.batchByDir(projectDir))) {
                batchCreator.createBatch(rating, out);
            }
        }
    }

    private Double severity(File basedir) throws IOException {
        double severitySum = 0;
        int count = 0;
        for (Processor processor : processors) {
            final Double severity = processor.calcSeverity(basedir, asList("/test/", "/target/"));
            if (severity != null) {
                severitySum += severity;
                count++;
            }
        }
        return count == 0 ? null : 1 / (1 + 100 * severitySum / count);
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
