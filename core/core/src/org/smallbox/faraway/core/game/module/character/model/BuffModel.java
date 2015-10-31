package org.smallbox.faraway.core.game.module.character.model;

import org.smallbox.faraway.core.game.model.ObjectModel;

/**
 * Created by Alex on 02/07/2015.
 */
public class BuffModel extends ObjectModel {
    public interface BuffListener {
        void onStart(BuffCharacterModel data);
        void onUpdate(BuffCharacterModel data, int update);
    }

    public String           _message;
    public int              _level;
    public int              _mood;
    private BuffListener    _listener;
    private boolean         _visible;

    public void setListener(BuffListener listener) { _listener = listener; }
    public void setLevel(int level) { _level = level; }
    public void setMood(int mood) { _mood = mood; }
    public void setMessage(String message) { _message = message; }
    public void setVisible(boolean visible) { _visible = visible; }

    public boolean  isVisible() { return _visible; }

    public void start(BuffCharacterModel data) { _listener.onStart(data); }
    public void update(BuffCharacterModel data, int update) { _listener.onUpdate(data, update); }
}
