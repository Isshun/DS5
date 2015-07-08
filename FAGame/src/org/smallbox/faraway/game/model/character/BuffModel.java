package org.smallbox.faraway.game.model.character;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Created by Alex on 02/07/2015.
 */
public class BuffModel {
    public LuaValue         luaCharacter;
    public Globals          globals;
    public String           message;
    public int              level;
    public int              mood;
}
