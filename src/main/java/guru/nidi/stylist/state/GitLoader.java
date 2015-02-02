package guru.nidi.stylist.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
@Service
public class GitLoader {
    private static final int LOCK_TIMEOUT = 60 * 60 * 1000;

    private final Locator locator;

    @Autowired
    public GitLoader(Locator locator) {
        this.locator = locator;
    }

    public void requestLoad(String url) {
        new Thread(() -> load(url)).start();
    }

    private void doInLock(String url, Runnable task) {
        final File lock = locator.lockByUrl(url);
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

    public void load(String url) {
        doInLock(url, () -> {
            final File target = locator.projectByUrl(url);
            if (target.exists()) {
                execute(target, "git", "pull");
            } else {
                execute(locator.projectsDir(), "git", "clone", url, target.getAbsolutePath());
            }
        });
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


}
