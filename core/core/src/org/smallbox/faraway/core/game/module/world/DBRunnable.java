package org.smallbox.faraway.core.game.module.world;

import com.almworks.sqlite4java.SQLiteConnection;

/**
 * Created by Alex on 10/11/2015.
 */
public interface DBRunnable {
    void run(SQLiteConnection db);
}
