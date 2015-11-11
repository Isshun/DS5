package org.smallbox.faraway.core.game.module.world;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 10/11/2015.
 */
public class SQLHelper {
    private static SQLHelper    _self;
    private Queue<DBRunnable>   _queue = new LinkedBlockingQueue<>();
    private SQLiteConnection    _db;

    public static SQLHelper getInstance() {
        if (_self == null) {
            _self = new SQLHelper();
        }
        return _self;
    }

    public void post(DBRunnable runnable) {
        _queue.add(runnable);
    }

    public void update() {
        if (!_queue.isEmpty()) {
            System.out.println("Exec DB runnable");
            _queue.poll().run(_db);
        }
    }

    public void setDB(final File file) {
        _queue.add(db -> {
            _db = new SQLiteConnection(file);
            try { _db.open(true); } catch (SQLiteException e) { e.printStackTrace(); }
        });
    }
}