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
        public int[]            ticksPerHour = {-1, 1000, 500, 250, 100, 50, 20, 1};
        public int              tickInterval = 10;
        public double           startGameTime = 7;
        public int              startSpeed = 1;
        public int              roofMaxDistance = 6;
        public int              inventoryMaxQuantity = 100;
        public int              storageMaxQuantity = 100;
        public int              environmentDistance = 5;
        public int              maxNearDistance = 32;
        public double           characterSpeed = 100;
        public double           repairByHour = 1;
    }

    public ApplicationConfigScreenInfo screen = new ApplicationConfigScreenInfo();
    public ApplicationConfigGameInfo game = new ApplicationConfigGameInfo();
    public boolean              launchGui = true;
    public double               uiScale = 1;
    public String               lang = "en";
}
