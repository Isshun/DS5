package org.smallbox.faraway.core.module.world;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationObject
public class SQLManager {
    private Queue<DBRunnable>   _queue = new LinkedBlockingQueue<>();
    private SQLiteConnection    _db;

    public void post(DBRunnable runnable) {
        _queue.add(runnable);
    }

    public void update() {
        if (!_queue.isEmpty()) {
            _queue.poll().run(_db);
        }
    }

    public void openDB(final File file) {
        _queue.add(db -> {
            assert _db == null;
            _db = new SQLiteConnection(file);
            try { _db.open(true); } catch (SQLiteException e) { e.printStackTrace(); }
        });
    }

    public void closeDB() {
        _queue.add(db -> {
            assert _db != null;
            _db.dispose();
            _db = null;
        });
    }
}