package org.smallbox.faraway.module.lua.luaModel;

import org.smallbox.faraway.engine.lua.LuaCrewModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.area.AreaType;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.base.JobModule;
import org.smallbox.faraway.module.lua.LuaModule;
import org.smallbox.faraway.ui.LuaDataModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;

import java.util.Collection;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaGameModel {
    public LuaDataModel.OnExtendListener _extendListener;

    public UserInterface            ui;
    public LuaCrewModel             crew;
    public LuaDataModel             data;
    public LuaEventsModel           events;
    public JobModule                jobs;
    public Collection<LuaModule>    luaModules;
    public Collection<GameModule>   modules;

    public LuaGameModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents, UserInterface userInterface, LuaDataModel.OnExtendListener extendListener) {
        ui = userInterface;
        crew = luaCrew;
        data = new LuaDataModel(extendListener, GameData.getData());
        events = luaEvents;
        jobs = ModuleHelper.getJobModule();
        luaModules = Game.getInstance().getLuaModuleManager().getModules();
        modules = Game.getInstance().getModules();
        _extendListener = extendListener;
    }

    public void setPlan(String plan) {
        UserInterface.getInstance().getInteraction().set(UserInteraction.Action.SET_PLAN, plan);
    }

    public void setArea(String area) {
        UserInterface.getInstance().getInteraction().set(UserInteraction.Action.SET_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void removeArea(String area) {
        UserInterface.getInstance().getInteraction().set(UserInteraction.Action.REMOVE_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void setBuild(ItemInfo itemInfo) {
        System.out.println("Set build from lua: " + itemInfo.name);
        UserInterface.getInstance().getInteraction().set(UserInteraction.Action.BUILD_ITEM, itemInfo);
    }

    public void clearAction() {
        UserInterface.getInstance().getInteraction().clean();
    }
}
