package org.smallbox.faraway.core.data.serializer;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.SQLHelper;

/**
 * Created by Alex on 19/11/2015.
 */
public class MainSerializer extends GameSerializer {

    @Override
    public void onSave(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                db.exec("CREATE TABLE game (key TEXT, i INTEGER, r REAL, t TEXT)");
                SQLiteStatement stInt = db.prepare("INSERT INTO game (key, i) VALUES (?, ?)");
                SQLiteStatement stReal = db.prepare("INSERT INTO game (key, r) VALUES (?, ?)");
                SQLiteStatement stText = db.prepare("INSERT INTO game (key, t) VALUES (?, ?)");
                try {
                    db.exec("begin transaction");
                    saveInt(stInt, "viewport_x", Game.getInstance().getViewport().getPosX());
                    saveInt(stInt, "viewport_y", Game.getInstance().getViewport().getPosY());
                    db.exec("end transaction");
                } finally {
                    stInt.dispose();
                    stReal.dispose();
                    stText.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveInt(SQLiteStatement stInt, String key, int value) throws SQLiteException {
        stInt.bind(1, key).bind(2, value);
        stInt.step();
        stInt.reset(false);
    }

    private void saveReal(SQLiteStatement stReal, String key, double value) throws SQLiteException {
        stReal.bind(1, key).bind(2, value);
        stReal.step();
        stReal.reset(false);
    }

    private void saveText(SQLiteStatement stText, String key, String value) throws SQLiteException {
        stText.bind(1, key).bind(2, value);
        stText.step();
        stText.reset(false);
    }

    public void onLoad(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                SQLiteStatement stInt = db.prepare("SELECT i FROM game WHERE key = ?");
                SQLiteStatement stReal = db.prepare("SELECT r FROM game WHERE key = ?");
                SQLiteStatement stText = db.prepare("SELECT t FROM game WHERE key = ?");
                try {
                    game.getViewport().setPosition(getInt(stInt, "viewport_x", 0), getInt(stInt, "viewport_y", 0));
                } finally {
                    stInt.dispose();
                    stReal.dispose();
                    stText.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    private int getInt(SQLiteStatement stInt, String key, int value) throws SQLiteException {
        stInt.bind(1, key);
        if (stInt.step()) {
            value = stInt.columnInt(0);
        }
        stInt.reset(false);
        return value;
    }

    private double getReal(SQLiteStatement stRealt, String key, double value) throws SQLiteException {
        stRealt.bind(1, key);
        if (stRealt.step()) {
            value = stRealt.columnDouble(0);
        }
        stRealt.reset(false);
        return value;
    }

    private String getText(SQLiteStatement stText, String key, String value) throws SQLiteException {
        stText.bind(1, key);
        if (stText.step()) {
            value = stText.columnString(0);
        }
        stText.reset(false);
        return value;
    }
}