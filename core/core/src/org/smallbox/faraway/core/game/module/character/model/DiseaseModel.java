package org.smallbox.faraway.core.game.module.character.model;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Created by Alex on 02/07/2015.
 */
public class DiseaseModel {
    public final String name;
    public String       message;
    public int          level;
    public LuaValue     data;
    public Globals      globals;
    public LuaValue     luaCharacter;

    public DiseaseModel(String name) {
        this.name = name;
    }
}
