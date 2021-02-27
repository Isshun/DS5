package org.smallbox.faraway.core.save;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.world.DBRunnable;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationObject
public class SQLManager {
    private final Queue<DBRunnable> queue = new LinkedBlockingQueue<>();
    private SQLiteConnection db;

    public void post(DBRunnable runnable) {
        queue.add(runnable);
    }

    public void update() {
        try {
            while (!queue.isEmpty()) {
                queue.poll().run(db);
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public void openDB(final File file) {
        queue.add(db -> {
            try {
                this.db = new SQLiteConnection(file);
                this.db.open(true);
            } catch (SQLiteException e) {
                Log.error(e);
            }
        });
    }

    public void closeDB() {
        queue.add(db -> {
            this.db.dispose();
            this.db = null;
        });
    }
}