package org.smallbox.faraway.core.engine.module.lua.luaModel;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Config;
import org.smallbox.faraway.core.engine.lua.LuaCrewModel;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.UserInterface;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaApplicationModel {
    public WorldModule              world;
    public long                     tick;
    public int                      day;
    public int                      hour;
    public int                      year;
    public UserInterface            ui;
    public LuaCrewModel             crew;
    public LuaEventsModel           events;
    public Game                     game;
    public Config                   config = new Config();
    public ApplicationInfo          info = new ApplicationInfo();
    public Collection<JobModel>     jobs;
    public Collection<LuaModule>    luaModules;
    public Collection<ModuleBase>   modules;
    public Collection<ModuleBase>   moduleThirds;

    public LuaApplicationModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents) {
        crew = luaCrew;
        events = luaEvents;
        luaModules = LuaModuleManager.getInstance().getModules();
        modules = ModuleManager.getInstance().getModules();
        moduleThirds = ModuleManager.getInstance().getModulesThird();
    }

    public void startGame(Game game) {
        jobs = ModuleHelper.getJobModule().getJobs();
        world = ModuleHelper.getWorldModule();
    }

    public void update() {
        this.game = Game.getInstance();
        this.tick = game.getTick();
        this.hour = game.getHour();
        this.day = game.getDay();
        this.year = game.getYear();
    }

    public ModuleBase getModule(String name) {
        Optional<ModuleBase> moduleOptional = this.modules.stream().filter(module -> module.getInfo().name.equals(name)).findFirst();
        return moduleOptional.isPresent() ? moduleOptional.get() : null;
    }

    public void setDisplay(String display) {
        Game.getInstance().setDisplay(display, true);
    }

    public void toggleDisplay(String display) {
        Game.getInstance().toggleDisplay(display);
    }

    public void setPlan(String plan) {
        Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_PLAN, plan);
    }

    public void destroy(MapObjectModel object) {
        Game.getInstance().getInteraction().planDestroy(object.getParcel());
    }

    public void cancel(MapObjectModel object) {
        Application.getInstance().notify(observer -> observer.onCancelJobs(object.getParcel(), object));
    }

    public void setSpeed(int speed) {
        if (GameManager.getInstance().isLoaded()) {
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
        Log.info("Set build from lua: " + itemInfo.name);
        Game.getInstance().getInteraction().set(GameActionExtra.Action.BUILD_ITEM, itemInfo);
    }

    public void stopGame() {
        this.game = null;
        GameManager.getInstance().stopGame();
    }

    public void resumeGame() {
        GameManager.getInstance().setRunning(true);
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
