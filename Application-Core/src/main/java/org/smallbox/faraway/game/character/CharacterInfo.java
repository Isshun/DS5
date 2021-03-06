package org.smallbox.faraway.game.character;

import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;

public class CharacterInfo extends ObjectInfo {
    public GraphicInfo graphic;

    public CharacterInfo(String name) {
        this.name = name;
    }

    public static class ChangeInfo {
        public double       rest;
        public double       work;
        public double       sleep;
        public double       sleepOnFloor;
    }

    public static class NeedInfo {
        public double       optimal;
        public double       warning;
        public double       critical;
        public ChangeInfo   change = new ChangeInfo();
    }

    public static class NeedsInfo {
        public NeedInfo     food = new NeedInfo();
        public NeedInfo     drink = new NeedInfo();
        public NeedInfo     energy = new NeedInfo();
        public NeedInfo     oxygen = new NeedInfo();
        public NeedInfo     relation = new NeedInfo();
        public NeedInfo     entertainment = new NeedInfo();
        public NeedInfo     happiness = new NeedInfo();
        public NeedInfo     heat = new NeedInfo();
    }

    public String           label;
    public int              index;
    public String           path;
    public int              thermolysis;
    public NeedsInfo needs = new NeedsInfo();
}
