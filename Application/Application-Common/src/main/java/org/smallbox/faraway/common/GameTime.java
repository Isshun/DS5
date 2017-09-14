package org.smallbox.faraway.common;

import java.util.Calendar;

/**
 * Created by Alex on 22/03/2017.
 */
public class GameTime {

    // Temps interne au jeu (en heure)
    private Calendar _calendar = Calendar.getInstance();
    private double _time;
    private int _minute;
    private int _hour;
    private int _day;
    private int _month;
    private int _year;

    public GameTime(double startTime) {
        _time = startTime;
    }

    public int getMinute() { return _minute; }
    public int getHour() { return _hour; }
    public int getDay() { return _day; }
    public int getMonth() { return _month; }
    public int getYear() { return _year; }

    public void add(double value) {
        _time += value;

        double totalMinutes = _time * 60;
        _minute = (int) (totalMinutes % 60);
        _hour = (int) (totalMinutes / 60 % 24);

        _calendar.set(2135, Calendar.JANUARY, 1, 0, 0, 0);
        _calendar.add(Calendar.MINUTE, (int) totalMinutes);
        _day = _calendar.get(Calendar.DAY_OF_MONTH);
        _month = _calendar.get(Calendar.MONTH) + 1;
        _year = _calendar.get(Calendar.YEAR);
    }

    public static double fromMinute(double value) {
        return value / 60;
    }

    public static double fromHour(double value) {
        return value;
    }
}
