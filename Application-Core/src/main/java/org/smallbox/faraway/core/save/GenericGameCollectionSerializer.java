package org.smallbox.faraway.core.save;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import org.smallbox.faraway.core.game.GameSerializer;

import java.util.Collection;

public abstract class GenericGameCollectionSerializer<T> extends GameSerializer {
    public abstract void onCreateTable(SQLiteConnection db) throws SQLiteException;
    public abstract void onSaveEntry(SQLiteConnection db, T entry) throws SQLiteException;
    public abstract void onLoadEntry(SQLiteConnection db) throws SQLiteException;
    public abstract Collection<T> getEntries();

    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                onCreateTable(db);
                getEntries().forEach(entry -> {
                    try {
                        db.exec("begin transaction");
                        onSaveEntry(db, entry);
                        db.exec("end transaction");
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                });
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                onLoadEntry(db);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

}
