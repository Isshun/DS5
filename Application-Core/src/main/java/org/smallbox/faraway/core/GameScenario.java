package org.smallbox.faraway.core;

import java.util.List;

public class GameScenario {

    public static class ScenarioCharacterEntity {
        public int x;
        public int y;
        public int z;
    }

    public static class ScenarioConsumableEntity {
        public String name;
        public int quantity;
        public int x;
        public int y;
        public int z;
        public int stack;
    }

    public static class ScenarioItemEntity {
        public String name;
        public int x;
        public int y;
        public int z;
    }

    public static class ScenarioPlantEntity {
        public String name;
        public int x;
        public int y;
        public int z;
    }

    public static class ScenarioResourceEntity {
        public String name;
        public int x;
        public int y;
        public int z;
    }

    public String planet;
    public String region;
    public int width;
    public int height;
    public int level;
    public int[] centerOnMap;
    public boolean generateMountains = true;
    public List<ScenarioCharacterEntity> characters;
    public List<ScenarioConsumableEntity> consumables;
    public List<ScenarioItemEntity> items;
    public List<ScenarioPlantEntity> plants;
    public List<ScenarioResourceEntity> resources;
}
