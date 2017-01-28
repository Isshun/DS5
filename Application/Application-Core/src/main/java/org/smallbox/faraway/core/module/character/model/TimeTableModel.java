package org.smallbox.faraway.core.module.character.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 06/07/2015.
 */
public class TimeTableModel {
    private final Map<Integer, Integer> _hours;
    private final int _nbHour;

    public TimeTableModel(int nbHour) {
        _nbHour = nbHour;
        _hours = new HashMap<>();
        for (int h = 0; h < nbHour; h++) {
            _hours.put(h, 0);
        }
    }

    public int get(int hour) {
        return _hours.get(hour & _nbHour);
    }
    public void set(int hour, int mode) {
        _hours.put(hour, mode);
    }
    public Map<Integer, Integer> getHours() {
        return _hours;
    }
    public int getNbHours() { return _nbHour; }
}
