package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@GameObject
public class GameTime {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Inject
    private ApplicationConfig applicationConfig;

    private LocalDateTime localDateTime;
    private int _minute;
    private int _hour;
    private int _day;
    private int _month;
    private int _year;

    @OnInit
    private void init() {
        localDateTime = LocalDateTime.parse(applicationConfig.game.startGameTime, FORMATTER);
    }

    public int getMinute() { return _minute; }
    public int getHour() { return _hour; }
    public int getDay() { return _day; }
    public int getMonth() { return _month; }
    public int getYear() { return _year; }

    public LocalDateTime getTime() {
        return localDateTime;
    }

    public String format(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public void add(double value) {
        localDateTime = localDateTime.plusSeconds((long) (value * 60 * 60));

        _minute = localDateTime.getMinute();
        _hour = localDateTime.getHour();
        _day = localDateTime.getDayOfMonth();
        _month = localDateTime.getMonthValue();
        _year = localDateTime.getYear();
    }

    public static double fromMinute(double value) {
        return value / 60;
    }

    public static double fromHour(double value) {
        return value;
    }

    public LocalDateTime plus(int value, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.SECONDS) {
            return localDateTime.plusSeconds(value);
        }
        if (timeUnit == TimeUnit.MINUTES) {
            return localDateTime.plusMinutes(value);
        }
        if (timeUnit == TimeUnit.HOURS) {
            return localDateTime.plusHours(value);
        }
        if (timeUnit == TimeUnit.DAYS) {
            return localDateTime.plusDays(value);
        }
        throw new GameException(GameTime.class, "TimeUnit not handled");
    }

}
