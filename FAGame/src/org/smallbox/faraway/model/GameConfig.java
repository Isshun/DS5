package org.smallbox.faraway.model;

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
        public int              socialize;
        public double           security;
    }

    public static class CharacterEffects {
        public EffectValues     regular;
        public EffectValues     sleepOnFloor;
        public EffectValues     starve;
        public EffectValues     exhausted;
    }
    public static class GameConfigCharacter {
        public CharacterEffects effects;
    }
    public static class GameConfigRender {
        public boolean          floor;
        public boolean          structure;
        public boolean          resource;
        public boolean          item;
    }

    public GameConfigRender     render;
    public GameConfigCharacter  character;
    public String               weather;
    public String               time;
}
