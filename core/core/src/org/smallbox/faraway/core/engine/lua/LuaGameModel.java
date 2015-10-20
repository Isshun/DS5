package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaGameModel {
    public final long               tick;
    public final int                day;
    public final int                hour;
    public final int                year;
    public final LuaMapModel        map;
    public final LuaCameraModel     camera;
    public final LuaCrewModel       crew;
    public final LuaVisitorModel    friendly;
    public final LuaVisitorModel    hostiles;
    public final LuaVisitorModel    neutrals;
    public final LuaFactoryModel    factory;

    public LuaGameModel(Game game) {
        this.map = new LuaMapModel();
        this.tick = game.getTick();
        this.hour = game.getHour();
        this.day = game.getDay();
        this.year = game.getYear();
        this.crew = new LuaCrewModel();
        this.camera = new LuaCameraModel();
        this.factory = new LuaFactoryModel();
        this.friendly = new LuaVisitorModel();
        this.hostiles = new LuaVisitorModel();
        this.neutrals = new LuaVisitorModel();
    }
}
