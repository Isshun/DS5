package org.smallbox.faraway.core.game.model;

/**
 * Created by Alex on 03/07/2015.
 */
public class CharacterTypeInfo {
    public static class ChangeInfo {
        public double       wake;
        public double       work;
        public double       sleep;
        public double       sleepOnFloor;
    }

    public static class NeedInfo {
        public double       optimal;
        public int          warning;
        public int          critical;
        public ChangeInfo   change;
    }

    public static class Needs {
        public NeedInfo     food;
        public NeedInfo     energy;
        public NeedInfo     oxygen;
        public NeedInfo     relation;
        public NeedInfo     joy;
        public NeedInfo     happiness;
        public NeedInfo     heat;
    }

    public String           name;
    public String           label;
    public Needs            needs;
    public int              index;
    public int              thermolysis;
}
