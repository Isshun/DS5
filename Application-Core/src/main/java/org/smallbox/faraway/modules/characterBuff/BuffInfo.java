package org.smallbox.faraway.modules.characterBuff;

import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.characterBuff.handler.BuffHandler;
import org.smallbox.faraway.modules.characterDisease.DiseaseInfo;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Alex on 02/07/2015.
 */
public class BuffInfo extends ObjectInfo {

    public interface OnGetLevel {
        int getLevel(CharacterModel character);
    }

    public OnGetLevel onGetLevel;

    public interface BuffListener {
        void onStart(CharacterBuff data);
        void onCheck(CharacterBuff data, int tick);
        void onUpdate(CharacterBuff data, int update);
        void onUpdateHourly(CharacterBuff data, int update);
    }

    public static class BuffLevelInfo {
        public String message;
        public int level;
        public int mood;
        public Collection<BuffEffectInfo> effects = new ConcurrentLinkedQueue<>();
    }

    public static class BuffEffectInfo {
        public DiseaseInfo disease;
        public Map<String, Double> needs = new ConcurrentHashMap<>();
        public double rate;
    }

    final public int id = UUID.randomUUID().toString().hashCode();
    public String           _message;
    public int              _level;
    public int              _mood;
    private BuffListener    _listener;
    private boolean         _visible;
    private int             _duration;
    private String          _name;
    public BuffHandler handler;
    public final Collection<BuffLevelInfo> levels = new CopyOnWriteArrayList<>();

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

    public void start(CharacterBuff data) { _listener.onStart(data); }
    public void check(CharacterBuff data, int tick) { _listener.onCheck(data, tick); }
    public void update(CharacterBuff data, int tick) { _listener.onUpdate(data, tick); }
    public void updateHourly(CharacterBuff data, int tick) { _listener.onUpdateHourly(data, tick); }
}
