package org.smallbox.faraway.game.model;

/**
 * Created by Alex on 03/07/2015.
 */
public class CharacterTypeInfo {
    public static class NeedInfo {
        public int          warning;
        public int          critical;
        public double       change;
    }

    public static class Needs {
        public NeedInfo     food;
        public NeedInfo     energy;
        public NeedInfo     oxygen;
        public NeedInfo     relation;
        public NeedInfo     joy;
    }

    public String           name;
    public String           label;
    public Needs needs;
}
