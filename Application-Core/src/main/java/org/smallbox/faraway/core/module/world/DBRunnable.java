package org.smallbox.faraway.core.module.world;

import com.almworks.sqlite4java.SQLiteConnection;

public interface DBRunnable {
    void run(SQLiteConnection db);
}
