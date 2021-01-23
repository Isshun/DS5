package org.smallbox.faraway.core.game;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import org.smallbox.faraway.core.module.world.SQLManager;

public abstract class GenericGameSerializer extends GameSerializer {
    public abstract void onCreateTable(SQLiteConnection db) throws SQLiteException;

    public abstract void onSave(SQLiteConnection db) throws SQLiteException;

    public abstract void onLoad(SQLiteConnection db) throws SQLiteException;

    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                onCreateTable(db);
                db.exec("begin transaction");
                onSave(db);
                db.exec("end transaction");
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                onLoad(db);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

}
