package guru.nidi.stylist.state;

import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
public class GitLoader {
    private static final int LOCK_TIMEOUT = 60 * 60 * 1000;

    private final Database database;
    private final File projectsDir;
    private final Crawler crawler;

    public GitLoader(Database database, File projectsDir, Crawler crawler) {
        this.database = database;
        this.projectsDir = projectsDir;
        this.crawler = crawler;
        projectsDir.mkdirs();
    }

    public void requestLoad(String url) {
        new Thread(() -> load(url)).start();
    }

    @Scheduled(fixedDelayString = "${git.period}")
    public void refresh() throws IOException {
        for (Project project : database.getProjects("origin")) {
            load(project.getOrigin());
        }
    }

    private void load(String url) {
        doInLock(url, () -> {
            final File target = projectByUrl(url);
            if (target.exists()) {
                execute(target, "git", "pull");
            } else {
                execute(projectsDir, "git", "clone", url, target.getAbsolutePath());
            }
            final int last = url.lastIndexOf('/');
            final Project project = new Project(url.substring(last + 1), target.getAbsolutePath(), url);
            crawler.apply(project);
            database.saveOrUpdate(project);
        });
    }

    private void doInLock(String url, Runnable task) {
        final File lock = lockByUrl(url);
        if (lock.exists()) {
            if (System.currentTimeMillis() - lock.lastModified() < LOCK_TIMEOUT) {
                return;
            }
            lock.delete();
        }
        try {
            lock.createNewFile();
        } catch (IOException e) {
            //ignore
        }
        try {
            task.run();
        } finally {
            lock.delete();
        }
    }

    private void execute(File dir, String... command) {
        String out = "";
        try {
            final Process git = new ProcessBuilder(command)
                    .directory(dir)
                    .redirectErrorStream(true)
                    .start();
            final BufferedReader in = new BufferedReader(new InputStreamReader(git.getInputStream()));
            while (in.ready()) {
                out += in.readLine() + "\n";
            }
            final int code = git.waitFor();
            if (code != 0) {
                throw new IOException("Return code was not 0 but " + code);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Could not execute git.\n" + out, e);
        }
    }

    public File projectByUrl(String url) {
        return new File(projectsDir, normalized(url));
    }

    public File lockByUrl(String url) {
        return new File(projectsDir, "." + normalized(url));
    }

    private String normalized(String url) {
        StringBuilder s = new StringBuilder(url);
        int pos = url.indexOf("://");
        if (pos >= 0) {
            s.delete(0, pos + 3);
        }
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                s.setCharAt(i, '-');
            }
        }
        return s.toString();
    }

}
