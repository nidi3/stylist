package guru.nidi.stylist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import guru.nidi.stylist.rating.checkstyle.CheckstyleProcessor;
import guru.nidi.stylist.state.Locator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;


@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class Application {
    @Value("${projects.dir}")
    private File projectsDir;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }

    @Bean
    public Locator locator() {
        return new Locator(projectsDir);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CheckstyleProcessor checkstyleProcessor() throws CheckstyleException {
        return new CheckstyleProcessor(Application.class.getResourceAsStream("/sun_checks.xml"));
    }

}