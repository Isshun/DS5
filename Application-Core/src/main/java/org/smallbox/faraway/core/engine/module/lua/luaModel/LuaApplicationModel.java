package org.smallbox.faraway.core.engine.module.lua.luaModel;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Config;
import org.smallbox.faraway.core.engine.lua.LuaCrewModel;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.UIManager;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaApplicationModel {
    public long                     tick;
    public int                      day;
    public int                      hour;
    public int                      year;
    public UIManager                ui;
    public LuaCrewModel             crew;
    public LuaEventsModel           events;
    public Game                     game;
    public Config                   config = new Config();
    public ApplicationInfo          info = new ApplicationInfo();
    public Collection<LuaModule>    luaModules;
    public Collection<ModuleBase>   modules;
    public Collection<ModuleBase>   moduleThirds;

    public LuaApplicationModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents) {
        crew = luaCrew;
        events = luaEvents;
        luaModules = Application.luaModuleManager.getModules();
        modules = Application.moduleManager.getModules();
        moduleThirds = Application.moduleManager.getModulesThird();
    }

    public void startGame(Game game) {
    }

    public void update() {
        this.game = Application.gameManager.getGame();
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
        Application.gameManager.getGame().setDisplay(display, true);
    }

    public void toggleDisplay(String display) {
        Application.gameManager.getGame().toggleDisplay(display);
    }

    public void setPlan(String plan) {
        Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_PLAN, plan);
    }

    public void destroy(MapObjectModel object) {
        Application.gameManager.getGame().getInteraction().planDestroy(object.getParcel());
    }

    public void cancel(MapObjectModel object) {
        Application.notify(observer -> observer.onCancelJobs(object.getParcel(), object));
    }

    public void setSpeed(int speed) {
        if (Application.gameManager.isLoaded()) {
            Application.gameManager.getGame().setSpeed(speed);
        }
    }

    public void setArea(String area) {
        Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void removeArea(String area) {
        Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void setBuild(ItemInfo itemInfo) {
        Log.info("Set build from lua: " + itemInfo.name);
        Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.BUILD_ITEM, itemInfo);
    }

    public void stopGame() {
        this.game = null;
        Application.gameManager.stopGame();
    }

    public void resumeGame() {
        Application.gameManager.setRunning(true);
    }

    public void exit() {
        Application.setRunning(false);
    }

    public void clearAction() {
        Application.gameManager.getGame().getInteraction().clear();
    }

    public void sendEvent(String tag) {
        Application.notify(observer -> observer.onCustomEvent(tag, null));
    }

    public void sendEvent(String tag, Object object) {
        Application.notify(observer -> observer.onCustomEvent(tag, object));
    }
}
