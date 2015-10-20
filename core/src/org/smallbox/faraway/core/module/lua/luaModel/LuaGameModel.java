package org.smallbox.faraway.core.module.lua.luaModel;

import org.smallbox.faraway.core.engine.lua.LuaCrewModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.area.AreaType;
import org.smallbox.faraway.core.game.model.item.ItemInfo;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.game.module.GameModule;
import org.smallbox.faraway.core.game.module.ModuleHelper;
import org.smallbox.faraway.core.game.module.ModuleManager;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.ui.UserInteraction;
import org.smallbox.faraway.core.ui.UserInterface;

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
    public Collection<JobModel> jobs;
    public Collection<LuaModule>    luaModules;
    public Collection<GameModule>   modules;
    public Collection<GameModule>   moduleThirds;

    public LuaGameModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents, UserInterface userInterface) {
        ui = userInterface;
        crew = luaCrew;
        events = luaEvents;
        jobs = ModuleHelper.getJobModule().getJobs();
        luaModules = Game.getInstance().getLuaModuleManager().getModules();
        modules = Game.getInstance().getModules();
        moduleThirds = ModuleManager.getInstance().getModulesThird();
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
