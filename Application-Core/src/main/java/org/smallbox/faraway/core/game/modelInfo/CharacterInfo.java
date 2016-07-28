package org.smallbox.faraway.core.game.modelInfo;

/**
 * Created by Alex on 03/07/2015.
 */
public class CharacterInfo extends ObjectInfo {
    public CharacterInfo(String name) {
        this.name = name;
    }

    public static class ChangeInfo {
        public double rest;
        public double       work;
        public double       sleep;
        public double       sleepOnFloor;
    }

    public static class NeedInfo {
        public double       optimal;
        public int          warning;
        public int          critical;
        public ChangeInfo   change = new ChangeInfo();
    }

    public static class Needs {
        public NeedInfo     water = new NeedInfo();
        public NeedInfo     food = new NeedInfo();
        public NeedInfo     energy = new NeedInfo();
        public NeedInfo     oxygen = new NeedInfo();
        public NeedInfo     relation = new NeedInfo();
        public NeedInfo     joy = new NeedInfo();
        public NeedInfo     happiness = new NeedInfo();
        public NeedInfo     heat = new NeedInfo();
    }

    public String           label;
    public int              index;
    public String           path;
    public int              thermolysis;
    public Needs            needs = new Needs();
}
