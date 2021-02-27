package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.util.GameException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@GameObject
public class GameTime {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Inject private ApplicationConfig applicationConfig;

    private LocalDateTime startGameTime;
    private LocalDateTime localDateTime;
    private int _minute;
    private int _hour;
    private int _day;
    private int _month;
    private int _year;

    @OnInit
    private void init() {
        startGameTime = LocalDateTime.parse(applicationConfig.game.startGameTime, FORMATTER);
        localDateTime = LocalDateTime.parse(applicationConfig.game.startGameTime, FORMATTER);
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public int getMinute() { return _minute; }
    public int getHour() { return _hour; }
    public int getDay() { return _day; }
    public int getMonth() { return _month; }
    public int getYear() { return _year; }

    public LocalDateTime now() {
        return localDateTime;
    }

    public LocalDateTime getStartGameTime() {
        return startGameTime;
    }

    public String format(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public void add(double value) {
        localDateTime = localDateTime.plusSeconds((long) (value * 60 * 60));

        if (localDateTime.getDayOfMonth() > 3) {
            localDateTime = localDateTime.plusMonths(1);
            localDateTime = localDateTime.minusDays(3);
        }

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

    public LocalDateTime plus(long value, TimeUnit timeUnit) {
        return plus(localDateTime, value, timeUnit);
    }

    public static LocalDateTime plus(LocalDateTime localDateTime, long value, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.MILLISECONDS) {
            return localDateTime.plus(value, ChronoUnit.MILLIS);
        }
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
