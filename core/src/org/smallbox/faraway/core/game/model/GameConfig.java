package org.smallbox.faraway.core.game.model;

import java.util.List;

/**
 * Created by Alex on 04/06/2015.
 */
public class GameConfig {

    public static class ScreenValues {
        public int[]            resolution;
        public String           mode;
    }

    public static class GameConfigRender {
        public boolean          floor;
        public boolean          structure;
        public boolean          resource;
        public boolean          item;
        public boolean          consumable;
        public boolean          temperature;
        public boolean          job;
        public boolean          area;
        public boolean          debug;
        public boolean          room;
        public boolean          light;
        public boolean          particle;
        public boolean          fauna;
    }

    public static class GameConfigManager {
        public boolean          quest;
        public boolean          room;
        public boolean          temperature;
        public boolean          weather;
        public boolean          oxygen;
        public boolean          power;
        public boolean          fauna;
        public boolean          light;
        public boolean          area;
        public boolean          flora;
    }

    public GameConfigRender     render;
    public GameConfigManager    manager;
    public String               lang;
    public String               weather;
    public String               time;
    public List<String>         mods;
    public int                  tickPerHour;
    public int                  inventoryMaxQuantity;
    public int                  storageMaxQuantity;
    public int                  environmentDistance;
    public boolean              byPassMenu;
    public int                  maxNearDistance;
    public double               uiScale;
    public ScreenValues         screen;
}
