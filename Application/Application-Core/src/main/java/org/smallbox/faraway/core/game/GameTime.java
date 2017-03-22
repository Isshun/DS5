package org.smallbox.faraway.core.game;

/**
 * Created by Alex on 22/03/2017.
 */
public class GameTime {

    // Temps interne au jeu (en heure)
    private double _time;
    private int _minute;
    private int _hour;
    private int _day;
    private int _year;

    public GameTime(double startTime) {
        _time = startTime;
    }

    public int getMinute() { return _minute; }
    public int getHour() { return _hour; }
    public int getDay() { return _day; }
    public int getYear() { return _year; }

    public void add(double value) {
        _time += value;

        double totalMinutes = _time * 60;
        _minute = (int) (totalMinutes % 60);
        _hour = (int) (totalMinutes / 60 % 24);
        _day = (int) (totalMinutes / 60 / 24 % 365);
        _year = (int) (totalMinutes / 60 / 24 / 365);
    }
}
