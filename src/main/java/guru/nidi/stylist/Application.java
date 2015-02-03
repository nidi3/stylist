package guru.nidi.stylist;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import guru.nidi.stylist.rating.BatchCreator;
import guru.nidi.stylist.rating.checkstyle.CheckstyleProcessor;
import guru.nidi.stylist.state.Database;
import guru.nidi.stylist.state.DatabaseIniter;
import guru.nidi.stylist.state.GitLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;


@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    public CheckstyleProcessor checkstyleProcessor() throws CheckstyleException {
        return new CheckstyleProcessor(Application.class.getResourceAsStream("/sun_checks.xml"));
    }

    @Bean
    public Database database(@Value("${db.config}") File config, @Value("${db.dir}") String dataDir, ObjectMapper mapper) {
        return new Database(config, dataDir, mapper);
    }

    @Bean
    @DependsOn("database")
    public DatabaseIniter databaseIniter(Database database, BatchCreator batchCreator, @Value("${db.clear}") boolean clear) {
        return new DatabaseIniter(database, batchCreator, clear);
    }

    @Bean
    public GitLoader gitLoader(Database database, @Value("${projects.dir}") File projectsDir) {
        return new GitLoader(database, projectsDir);
    }

}