package org.smallbox.faraway.game.character;

import org.smallbox.faraway.game.character.model.base.CharacterExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CharacterTimetableExtra extends CharacterExtra {

    public CharacterTimetableExtra(CharacterModel character) {
        super(character);
    }

    public enum State { FREE, WORK, SLEEP }

    private final Map<Integer, State> _states = new ConcurrentHashMap<>();

    public State getState(int hour) {
        if (_states.containsKey(hour)) {
            return _states.get(hour);
        }
        return State.FREE;
    }

    public void nextState(int hour) {
        switch (getState(hour)) {
            case FREE: _states.put(hour, State.WORK); break;
            case WORK: _states.put(hour, State.SLEEP); break;
            case SLEEP: _states.put(hour, State.FREE); break;
        }

    }

    public void setState(int hour, State state) {
        _states.put(hour, state);
    }

}
