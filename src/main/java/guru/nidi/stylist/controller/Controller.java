package guru.nidi.stylist.controller;

import guru.nidi.stylist.state.Database;
import guru.nidi.stylist.state.GitLoader;
import guru.nidi.stylist.state.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Controller {
    private final Database database;
    private final GitLoader gitLoader;

    @Autowired
    public Controller(Database database, GitLoader gitLoader) {
        this.database = database;
        this.gitLoader = gitLoader;
    }

    @RequestMapping("/rating")
    public String rating(@RequestParam String url) {
        final Project project = database.findProjectByOrigin(url, "severity");
        if (project == null) {
            gitLoader.requestLoad(url);
        }
        return database.getBatch(project == null || project.getSeverity() == null
                ? null
                : (int) Math.round(100 * project.getSeverity()));
    }

    @RequestMapping("/list")
    public List<Project> list() {
        return database.getProjects("name,origin,severity").stream()
                .filter(p -> p.getSeverity() != null)
                .sorted((a, b) -> b.getSeverity() - a.getSeverity() < 0 ? -1 : 1)
                .collect(Collectors.toList());
    }

}