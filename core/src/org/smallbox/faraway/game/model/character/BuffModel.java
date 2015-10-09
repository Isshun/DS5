package org.smallbox.faraway.game.model.character;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.model.ObjectModel;
import org.smallbox.faraway.ui.engine.OnClickListener;

/**
 * Created by Alex on 02/07/2015.
 */
public class BuffModel extends ObjectModel {
    public LuaValue         luaCharacter;
    public Globals          globals;
    public String           message;
    public int              level;
    public int              mood;
    public OnClickListener  onClickListener;
}
