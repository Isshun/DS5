package org.smallbox.faraway.core.game;

/**
 * Created by Alex on 22/11/2015.
 */
public class ApplicationConfig {

    public static class ApplicationConfigScreenInfo {
        public int[]            resolution = new int[] {1400, 850};
        public int[]            position = new int[] {40, 20};
        public String           mode = "window";
        public int              foregroundFPS = 60;
        public int              backgroundFPS = 30;
    }

    public static class ApplicationConfigGameInfo {
        public int[]            ticksPerHour = {-1, 5000, 2000, 1000, 100, 50, 20, 10, 1};
        public int              tickInterval = 10;
        public double           startGameTime = GameTime.fromHour(7);
        public int              startSpeed = 1;
        public int              roofMaxDistance = 6;
        public int              inventoryMaxQuantity = 100;
        public int              storageMaxQuantity = 100;
        public int              environmentDistance = 5;
        public int              maxNearDistance = 32;
        public double           characterSpeed = 100;

        // Temps nécéssaire pour construire 1 point de 'item cost'
        public double           craftTime = GameTime.fromMinute(1);

        // Temps nécéssaire pour construire 1 point de 'item cost'
        public double           buildTime = GameTime.fromMinute(1);

        // Temps nécéssaire pour restaurer 1 point de vie
        public double           repairTime = GameTime.fromMinute(1);

        // Temps de base pour miner un terrain
        public double           digTime = GameTime.fromMinute(15);
    }

    public ApplicationConfigScreenInfo screen = new ApplicationConfigScreenInfo();
    public ApplicationConfigGameInfo game = new ApplicationConfigGameInfo();
    public boolean              launchGui = true;
    public double               uiScale = 1;
    public String               lang = "en";
}
