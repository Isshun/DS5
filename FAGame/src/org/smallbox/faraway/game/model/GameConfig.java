package org.smallbox.faraway.game.model;

/**
 * Created by Alex on 04/06/2015.
 */
public class GameConfig {
    public static class EffectValues {
        public double		    food;
        public double 		    drink;
        public double 		    energy;
        public double 		    happiness;
        public double 		    health;
        public double 		    relation;
        public double           oxygen;
        public double           socialize;
        public double           security;
        public double           joy;
    }

    public static class ScreenValues {
        public int[]            resolution;
        public String           mode;
    }

    public static class CharacterEffects {
        public EffectValues     human;
        public EffectValues     android;
        public EffectValues     droid;
    }

    public static class CharacterNeeds {
        public int[]            food;
        public int[]            oxygen;
    }

    public static class CharacterType {
        public CharacterNeeds   needs;
    }

    public static class GameConfigCharacter {
        public CharacterEffects effects;
        public CharacterType    human;
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
    }

    public static class GameConfigManager {
        public boolean          quest;
        public boolean          room;
        public boolean          temperature;
        public boolean          weather;
        public boolean          oxygen;
        public boolean          power;
        public boolean          fauna;
    }

    public GameConfigRender     render;
    public GameConfigManager    manager;
    public GameConfigCharacter  character;
    public String               lang;
    public String               weather;
    public String               time;
    public int                  tickPerHour;
    public int                  inventoryMaxQuantity;
    public int                  storageMaxQuantity;
    public int                  environmentDistance;
    public boolean              byPassMenu;
    public int                  maxNearDistance;
    public double               uiScale;
    public ScreenValues         screen;
}
