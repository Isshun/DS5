package org.smallbox.faraway.core.config;

import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.game.GameTime;

@ApplicationObject
public class ApplicationConfig {

    public static class ApplicationConfigDebug {
        public enum ActionOnLoad {NONE, LAST_SAVE, NEW_GAME}
        public ActionOnLoad actionOnLoad;
        public String scenario;
        public boolean log;
        public int logFontSize;
        public int logLineLength;
        public int logLineNumber;
        public boolean debugView;

        // ERROR / WARNING / INFO / DEBUG
        public String logLevel;
    }

    public static class ApplicationConfigScreenInfo {
        public int[]            resolution;
        public int[][]          acceptedResolutions43;
        public int[][]          acceptedResolutions169;
        public int[][]          acceptedResolutions1610;
        public int[]            position;
        public String           mode;
        public int              foregroundFPS;
        public int              backgroundFPS;
    }

    public static class ApplicationConfigUI {
        public int              panelWidth;
    }

    public static class ApplicationConfigGameInfo {
        public int[]            ticksPerHour;
        public int              tickInterval;
        public String           startGameTime;
        public int              startSpeed;
        public int              roofMaxDistance;
        public int              inventoryMaxQuantity;
        public int              storageMaxQuantity;
        public int              environmentDistance;
        public int              maxNearDistance;
        public double           characterSpeed;
        public int              minuteBeforeIdleJob;

        // Temps nécéssaire pour construire 1 point de 'item cost'
        public double           craftTime;

        // Temps nécéssaire pour construire 1 point de 'item cost'
        public double           buildTime;

        // Temps nécéssaire pour restaurer 1 point de vie
        public double           repairTime;

        // Temps de base pour miner un terrain
        public double           digTime = GameTime.fromMinute(15);
    }

    public ApplicationConfigDebug debug;
    public ApplicationConfigScreenInfo screen;
    public ApplicationConfigGameInfo game;
    public ApplicationConfigUI ui;
    public boolean              launchGui;
    public double               uiScale;
    public String               lang;
    public float musicVolume;
    public float soundVolume;

    public int getResolutionWidth() {
        return screen.resolution[0];
    }

    public int getResolutionHeight() {
        return screen.resolution[1];
    }

}
