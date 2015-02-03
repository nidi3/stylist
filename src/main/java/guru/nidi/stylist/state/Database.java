package guru.nidi.stylist.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class Database {
    private final File config;
    private final String dataDir;
    private final ObjectMapper mapper;

    private OServer server;
    private ODatabase<?> db;
    private ODatabaseDocumentTx tx;

    public Database(File config, String dataDir, ObjectMapper mapper) {
        this.config = config;
        this.dataDir = dataDir;
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() throws Exception {
        server = OServerMain.create();
        server.startup(config);
        server.activate();

        tx = new ODatabaseDocumentTx("plocal:" + dataDir);
        db = tx.exists() ? tx.open("admin", "admin") : tx.create();
    }

    boolean createClass(String name) {
        final boolean create = !db.getMetadata().getSchema().existsClass(name);
        if (create) {
            tx.command(new OCommandSQL("create class " + name)).execute();
            tx.commit();
        }
        return create;
    }

    boolean removeClass(String name) {
        final boolean remove = db.getMetadata().getSchema().existsClass(name);
        if (remove) {
            tx.command(new OCommandSQL("drop class " + name)).execute();
            tx.commit();
        }
        return remove;
    }

    @PreDestroy
    public void end() {
        db.close();
        server.shutdown();
    }

    public void update(Project project) {
        saveImpl(tx.<List<ODocument>>query(new OSQLSynchQuery<>("select from Project where name=?"), project.getName()).get(0), project);
    }

    public void save(Project project) {
        ODatabaseRecordThreadLocal.INSTANCE.set(tx);
        saveImpl(new ODocument("Project"), project);
    }

    private void saveImpl(ODocument doc, Project project) {
        try {
            doc.fromJSON(mapper.writeValueAsString(project));
            doc.save();
            tx.commit();
        } catch (JsonProcessingException e) {
            throw new DatabaseException(e);
        }
    }

    public List<Project> getProjects() {
        return tx.<List<ODocument>>query(new OSQLSynchQuery<>("select from Project")).stream()
                .map(doc -> toDomain(doc, Project.class))
                .collect(Collectors.toList());
    }

    public Project findProjectByOrigin(String origin, boolean withRatings) {
        final String fields = withRatings ? "" : " name,dir,origin,severity";
        final List<ODocument> docs = tx.<List<ODocument>>query(new OSQLSynchQuery<>("select" + fields + " from Project where origin=?"), origin);
        return docs.isEmpty() ? null : toDomain(docs.get(0), Project.class);
    }

    public String getBatch(Integer rating) {
        final List<ODocument> docs = tx.<List<ODocument>>query(new OSQLSynchQuery<>("select from Batch where rating=?"), normalizedRating(rating));
        return docs.get(0).field("svg");
    }

    public void saveBatch(Integer rating, String svg) {
        final ODocument doc = new ODocument("Batch")
                .field("rating", normalizedRating(rating))
                .field("svg", svg);
        doc.save();
        tx.commit();
    }

    private int normalizedRating(Integer rating) {
        return rating == null ? -1 : rating;
    }

    private <T> T toDomain(ODocument doc, Class<T> clazz) {
        try {
            return mapper.readValue(doc.toJSON("indent:0"), clazz);
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(Throwable cause) {
            super(cause);
        }
    }
}
