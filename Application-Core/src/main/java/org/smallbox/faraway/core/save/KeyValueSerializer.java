package org.smallbox.faraway.core.save;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.renderer.WorldCameraManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.util.GameException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@GameObject
public class KeyValueSerializer extends GenericGameSerializer {
    @Inject private WorldCameraManager worldCameraManager;
    @Inject private MapRenderer mapRenderer;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private GameTime gameTime;
    @Inject private DataManager dataManager;

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE game_info (key TEXT, value INTEGER)");
    }

    @Override
    public void onSave(SQLiteConnection db) throws SQLiteException {
        insert(db, "camera_x", viewport.getPosX());
        insert(db, "camera_y", viewport.getPosY());
        insert(db, "camera_z", viewport.getFloor());
        insert(db, "zoom", worldCameraManager.getZoom());
        insert(db, "game_time", gameTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
        worldCameraManager.setZoom(readFloat(db, "zoom"));
        viewport.setPosition(readInt(db, "camera_x"), readInt(db, "camera_y"));
        viewport.setFloor(readInt(db, "camera_z"));
        gameTime.setLocalDateTime(LocalDateTime.parse(readString(db, "game_time"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private String readString(SQLiteConnection db, String key) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("SELECT key, value FROM game_info");
        try {
            while (stItem.step()) {
                if (StringUtils.equals(stItem.columnString(0), key)) {
                    return stItem.columnString(1);
                }
            }
        } finally {
            stItem.dispose();
        }
        throw new GameException(KeyValueSerializer.class, "Unable to read game info: " + key);
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
        throw new GameException(KeyValueSerializer.class, "Unable to read game info: " + key);
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
        throw new GameException(KeyValueSerializer.class, "Unable to read game info: " + key);
    }

}
