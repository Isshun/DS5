package org.smallbox.faraway.core.game.service.applicationConfig;

import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.game.GameTime;

public class ApplicationConfig {

    public static class ApplicationConfigDebug {
        public enum ActionOnLoad {NONE, LAST_SAVE, NEW_GAME}
        public ActionOnLoad actionOnLoad;
        public String scenario;
    }

    public static class ApplicationConfigScreenInfo {
        public int[]            resolution;
        public int[]            position;
        public String           mode;
        public int              foregroundFPS;
        public int              backgroundFPS;
    }

    public static class ApplicationConfigGameInfo {
        public int[]            ticksPerHour;
        public int              tickInterval;
        public double           startGameTime;
        public int              startSpeed;
        public int              roofMaxDistance;
        public int              inventoryMaxQuantity;
        public int              storageMaxQuantity;
        public int              environmentDistance;
        public int              maxNearDistance;
        public double           characterSpeed;

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
    public boolean              launchGui;
    public double               uiScale;
    public String               lang;
}
