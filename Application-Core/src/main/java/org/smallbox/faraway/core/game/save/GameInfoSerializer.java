package org.smallbox.faraway.core.game.save;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GenericGameSerializer;

@GameObject
public class GameInfoSerializer extends GenericGameSerializer {
    @Inject private GDXRenderer gdxRenderer;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private Data data;

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE game_info (key TEXT, value INTEGER)");
    }

    @Override
    public void onSave(SQLiteConnection db) throws SQLiteException {
        insert(db, "camera_x", viewport.getPosX());
        insert(db, "camera_y", viewport.getPosY());
        insert(db, "camera_z", viewport.getFloor());
        insert(db, "zoom", gdxRenderer.getZoom());
    }

    private void insert(SQLiteConnection db, String key, Object value) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("INSERT INTO game_info (key, value) VALUES (?, ?)");
        try {
            stItem.bind(1, key);
            if (value instanceof Long) stItem.bind(2, (int) value);
            if (value instanceof Integer) stItem.bind(2, (int) value);
            if (value instanceof String) stItem.bind(2, (String) value);
            if (value instanceof Double) stItem.bind(2, (Double) value);
            if (value instanceof Float) stItem.bind(2, (Float) value);
            stItem.step();
        } finally {
            stItem.dispose();
        }
    }

    @Override
    public void onLoad(SQLiteConnection db) throws SQLiteException {
        gdxRenderer.setZoom(readFloat(db, "zoom"));
        viewport.setPositionX(readInt(db, "camera_x"));
        viewport.setPositionY(readInt(db, "camera_y"));
        viewport.setFloor(readInt(db, "camera_z"));
    }

    private int readInt(SQLiteConnection db, String key) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("SELECT key, value FROM game_info");
        try {
            while (stItem.step()) {
                if (StringUtils.equals(stItem.columnString(0), key)) {
                    return stItem.columnInt(1);
                }
            }
        } finally {
            stItem.dispose();
        }
        throw new GameException(GameInfoSerializer.class, "Unable to read game info: " + key);
    }

    private float readFloat(SQLiteConnection db, String key) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("SELECT key, value FROM game_info");
        try {
            while (stItem.step()) {
                if (StringUtils.equals(stItem.columnString(0), key)) {
                    return (float)stItem.columnDouble(1);
                }
            }
        } finally {
            stItem.dispose();
        }
        throw new GameException(GameInfoSerializer.class, "Unable to read game info: " + key);
    }

}
