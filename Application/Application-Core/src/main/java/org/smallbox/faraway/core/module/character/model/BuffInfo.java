package org.smallbox.faraway.core.module.character.model;

import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;

import java.util.UUID;

/**
 * Created by Alex on 02/07/2015.
 */
public class BuffInfo extends ObjectInfo {
    public interface BuffListener {
        void onStart(BuffCharacterModel data);
        void onCheck(BuffCharacterModel data, int tick);
        void onUpdate(BuffCharacterModel data, int update);
        void onUpdateHourly(BuffCharacterModel data, int update);
    }

    final public int id = UUID.randomUUID().toString().hashCode();
    public String           _message;
    public int              _level;
    public int              _mood;
    private BuffListener    _listener;
    private boolean         _visible;
    private int             _duration;
    private String          _name;

    public void setListener(BuffListener listener) { _listener = listener; }
    public void setLevel(int level) { _level = level; }
    public void setMood(int mood) { _mood = mood; }
    public void setMessage(String message) { _message = message; }
    public void setVisible(boolean visible) { _visible = visible; }
    public void setDuration(int duration) { _duration = duration; }
    public void setName(String name) { _name = name; }

    public boolean  isVisible() { return _visible; }
    public int      getDuration() { return _duration; }
    public String   getName() { return _name; }

    public void start(BuffCharacterModel data) { _listener.onStart(data); }
    public void check(BuffCharacterModel data, int tick) { _listener.onCheck(data, tick); }
    public void update(BuffCharacterModel data, int tick) { _listener.onUpdate(data, tick); }
    public void updateHourly(BuffCharacterModel data, int tick) { _listener.onUpdateHourly(data, tick); }
}
