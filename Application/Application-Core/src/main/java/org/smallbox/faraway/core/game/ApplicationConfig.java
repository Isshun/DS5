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
        public int              inventoryMaxQuantity = 100;
        public int              storageMaxQuantity = 100;
        public int              environmentDistance = 5;
        public int              maxNearDistance = 32;
        public int              tickPerHour = 50;
        public int              updateInterval = 500;
    }

    public ApplicationConfigScreenInfo screen = new ApplicationConfigScreenInfo();
    public ApplicationConfigGameInfo game = new ApplicationConfigGameInfo();
    public double               uiScale = 1;
    public String               lang = "en";
}
