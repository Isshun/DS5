package org.smallbox.faraway;

import org.luaj.vm2.Globals;
import org.smallbox.faraway.engine.lua.LuaCrewModel;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.ui.LuaDataModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaGameModel {
    public OnExtendListener _extendListener;
    public UserInterface    ui;
    public LuaCrewModel     crew;
    public LuaDataModel     data;
    public LuaEventsModel   events;

    public LuaGameModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents, UserInterface userInterface, OnExtendListener extendListener) {
        ui = userInterface;
        crew = luaCrew;
        data = new LuaDataModel(extendListener, GameData.getData());
        events = luaEvents;
        _extendListener = extendListener;
    }

    public void setPlan(String plan) {
        UserInterface.getInstance().getInteraction().set(UserInteraction.Action.SET_PLAN, plan);
    }

    public void setBuild(ItemInfo itemInfo) {
        System.out.println("Set build from lua: " + itemInfo.name);
        UserInterface.getInstance().getInteraction().set(UserInteraction.Action.BUILD_ITEM, itemInfo);
    }

    public void clearAction() {
        UserInterface.getInstance().getInteraction().clean();
    }
}
