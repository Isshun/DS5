package org.smallbox.faraway.module.lua.luaModel;

import org.smallbox.faraway.engine.lua.LuaCrewModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.area.AreaType;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.base.JobModule;
import org.smallbox.faraway.module.lua.LuaModule;
import org.smallbox.faraway.ui.LuaDataModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaGameModel {
    public long                     tick;
    public int                      day;
    public int                      hour;
    public int                      year;
    public UserInterface            ui;
    public LuaCrewModel             crew;
    public LuaEventsModel           events;
    public Collection<BaseJobModel> jobs;
    public Collection<LuaModule>    luaModules;
    public Collection<GameModule>   modules;

    public LuaGameModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents, UserInterface userInterface) {
        ui = userInterface;
        crew = luaCrew;
        events = luaEvents;
        jobs = ModuleHelper.getJobModule().getJobs();
        luaModules = Game.getInstance().getLuaModuleManager().getModules();
        modules = Game.getInstance().getModules();
    }

    public void update() {
        Game game = Game.getInstance();
        this.tick = game.getTick();
        this.hour = game.getHour();
        this.day = game.getDay();
        this.year = game.getYear();
    }

    public GameModule   getModule(String name) {
        Optional<GameModule> moduleOptional = this.modules.stream().filter(module -> module.getInfo().name.equals(name)).findFirst();
        return moduleOptional.isPresent() ? moduleOptional.get() : null;
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
