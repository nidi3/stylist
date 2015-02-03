package guru.nidi.stylist.state;

import guru.nidi.stylist.rating.BatchCreator;

import javax.annotation.PostConstruct;

/**
 *
 */
public class DatabaseIniter {
    private final Database database;
    private final BatchCreator batchCreator;
    private final boolean clear;

    public DatabaseIniter(Database database, BatchCreator batchCreator, boolean clear) {
        this.database = database;
        this.batchCreator = batchCreator;
        this.clear = clear;
    }

    @PostConstruct
    public void init() {
        if (clear) {
            database.removeClass("Project");
            database.removeClass("Batch");
        }
        database.createClass("Project");
        if (database.createClass("Batch")) {
            for (int i = 0; i <= 100; i++) {
                database.saveBatch(i, batchCreator.createBatch(i / 100d));
            }
            database.saveBatch(null, batchCreator.createUnknown());
        }
    }
}
