package org.smallbox.faraway.core.save;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.game.GameSerializer;

import java.util.Collection;

public abstract class GenericGameCollectionSerializer<T> extends GameSerializer {
    private final String createTableCmd;
    private final String insertCommand;
    private final String selectCommand;

    public abstract void onSaveEntry(SQLiteStatement statement, T entry) throws SQLiteException;
    public abstract void onLoadEntry(SQLiteStatement statement) throws SQLiteException;
    public abstract Collection<T> getEntries();

    public GenericGameCollectionSerializer(String createTableCmd, String insertCommand, String selectCommand) {
        this.createTableCmd = createTableCmd;
        this.insertCommand = insertCommand;
        this.selectCommand = selectCommand;
    }

    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                db.exec(createTableCmd);
                SQLiteStatement statement = db.prepare(insertCommand);

                try {
                    getEntries().forEach(entry -> {
                        try {
                            db.exec("begin transaction");

                            onSaveEntry(statement, entry);
                            if (statement.hasBindings()) {
                                statement.step();
                            }

                            db.exec("end transaction");
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    });
                } finally {
                    statement.dispose();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                SQLiteStatement statement = db.prepare(selectCommand);
                try {
                    while (statement.step()) {
                        onLoadEntry(statement);
                    }
                } finally {
                    statement.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

}
