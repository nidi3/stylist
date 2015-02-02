package guru.nidi.stylist.controller;

import guru.nidi.stylist.state.Crawler;
import guru.nidi.stylist.state.GitLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Controller {
    private final Crawler crawler;
    private final GitLoader gitLoader;

    @Autowired
    public Controller(Crawler crawler, GitLoader gitLoader) {
        this.crawler = crawler;
        this.gitLoader = gitLoader;
    }

    @RequestMapping("/rating")
    public FileSystemResource rating(@RequestParam String url) throws IOException {
        if (crawler.getStatus(url) == Crawler.Status.NO_PROJECT) {
            gitLoader.requestLoad(url);
        }
        return new FileSystemResource(crawler.getBatch(url));
    }

}