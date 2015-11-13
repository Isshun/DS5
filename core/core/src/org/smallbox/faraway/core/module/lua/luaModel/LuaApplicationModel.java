package org.smallbox.faraway.core.module.lua.luaModel;

import org.luaj.vm2.LuaTable;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Config;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.lua.LuaCrewModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.UserInterface;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaApplicationModel {
    public final WorldModule        world;
    public long                     tick;
    public int                      day;
    public int                      hour;
    public int                      year;
    public UserInterface            ui;
    public LuaCrewModel             crew;
    public LuaEventsModel           events;
    public Game                     game;
    public Config                   config;
    public ApplicationInfo          info = new ApplicationInfo();
    public LuaTable                 bindings = new LuaTable();
    public Collection<JobModel>     jobs;
    public Collection<LuaModule>    luaModules;
    public Collection<GameModule>   modules;
    public Collection<GameModule>   moduleThirds;

    public LuaApplicationModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents, UserInterface userInterface) {
        ui = userInterface;
        crew = luaCrew;
        events = luaEvents;
        jobs = ModuleHelper.getJobModule().getJobs();
        world = ModuleHelper.getWorldModule();
        luaModules = LuaModuleManager.getInstance().getModules();
        modules = ModuleManager.getInstance().getModules();
        moduleThirds = ModuleManager.getInstance().getModulesThird();
    }

    public void update() {
        this.game = Game.getInstance();
        this.tick = game.getTick();
        this.hour = game.getHour();
        this.day = game.getDay();
        this.year = game.getYear();
    }

    public GameModule   getModule(String name) {
        Optional<GameModule> moduleOptional = this.modules.stream().filter(module -> module.getInfo().name.equals(name)).findFirst();
        return moduleOptional.isPresent() ? moduleOptional.get() : null;
    }

    public void setDisplay(String display) {
        Game.getInstance().setDisplay(display);
    }

    public void setPlan(String plan) {
        Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_PLAN, plan);
    }

    public void setSpeed(int speed) {
        if (GameManager.getInstance().isRunning()) {
            GameManager.getInstance().getGame().setSpeed(speed);
        }
    }

    public void setArea(String area) {
        Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void removeArea(String area) {
        Game.getInstance().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void setBuild(ItemInfo itemInfo) {
        System.out.println("Set build from lua: " + itemInfo.name);
        Game.getInstance().getInteraction().set(GameActionExtra.Action.BUILD_ITEM, itemInfo);
    }

    public void stopGame() {
        this.game = null;
        GameManager.getInstance().stopGame();
    }

    public void resumeGame() {
        GameManager.getInstance().setPause(false);
    }

    public void exit() {
        Application.getInstance().setRunning(false);
    }

    public void clearAction() {
        Game.getInstance().getInteraction().clear();
    }

    public void sendEvent(String tag) {
        Application.getInstance().notify(observer -> observer.onCustomEvent(tag, null));
    }

    public void sendEvent(String tag, Object object) {
        Application.getInstance().notify(observer -> observer.onCustomEvent(tag, object));
    }

}
