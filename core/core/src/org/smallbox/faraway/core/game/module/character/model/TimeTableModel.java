package org.smallbox.faraway.core.game.module.character.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 06/07/2015.
 */
public class TimeTableModel {
    private final Map<Integer, Integer> _hours;

    public TimeTableModel(int nbHour) {
        _hours = new HashMap<>();
        for (int h = 0; h < nbHour; h++) {
            _hours.put(h, 0);
        }
    }

    public int get(int hour) {
        return _hours.get(hour);
    }

    public void set(int hour, int mode) {
        _hours.put(hour, mode);
    }

    public Map<Integer, Integer> getHours() {
        return _hours;
    }

}
