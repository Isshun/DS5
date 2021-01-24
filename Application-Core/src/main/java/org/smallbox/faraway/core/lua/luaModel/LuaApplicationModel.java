package org.smallbox.faraway.core.lua.luaModel;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.lua.ServerLuaModuleManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.core.module.ModuleManager;
import org.smallbox.faraway.core.lua.LuaModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.MapObjectModel;

import java.util.Collection;

@ApplicationObject
public class LuaApplicationModel {
    @Inject private ServerLuaModuleManager serverLuaModuleManager;
    @Inject private ModuleManager moduleManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private GameManager gameManager;

    @Inject
    public Game game;
    @Inject private GameTime gameTime;

    public long tick;
    public int day;
    public int hour;
    public int year;
    public int screen_width;
    public int screen_height;
    public Collection<LuaModule> luaModules;
    public Collection<ModuleBase> modules;
    public Collection<ModuleBase> moduleThirds;

    @OnInit
    public void init() {
        luaModules = serverLuaModuleManager.getModules();
        modules = moduleManager.getModules();
        moduleThirds = moduleManager.getModulesThird();
        screen_width = applicationConfig.getResolutionWidth();
        screen_height = applicationConfig.getResolutionHeight();
    }

    public void update() {
        this.tick = game.getTick();
        this.hour = gameTime.getHour();
        this.day = gameTime.getDay();
        this.year = gameTime.getYear();
    }

    public ModuleBase getModule(String name) {
        return this.modules.stream().filter(module -> module.getInfo().name.equals(name)).findFirst().orElse(null);
    }

    public void setDisplay(String display) {
        game.setDisplay(display, true);
    }

    public void toggleDisplay(String display) {
        game.toggleDisplay(display);
    }

    public void setPlan(String plan) {
        throw new NotImplementedException("");
//        game.getInteraction().set(GameActionExtra.Action.SET_PLAN, plan);
    }

    public void destroy(MapObjectModel object) {
        throw new NotImplementedException("");
//        game.getInteraction().planDestroy(object.getParcel());
    }

    public void cancel(MapObjectModel object) {
        Application.notify(observer -> observer.onCancelJobs(object.getParcel(), object));
    }

    public void setSpeed(int speed) {
        if (gameManager.isLoaded()) {
            game.setSpeed(speed);
        }
    }

    public void setArea(String area) {
        throw new NotImplementedException("");
//        game.getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void removeArea(String area) {
        throw new NotImplementedException("");
//        game.getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.valueOf(area.toUpperCase()));
    }

    public void setBuild(ItemInfo itemInfo) {
        throw new NotImplementedException("");
//        Log.info("Set build from lua: " + itemInfo.name);
//        game.getInteraction().set(GameActionExtra.Action.BUILD_ITEM, itemInfo);
    }

    public void exit() {
        Application.setRunning(false);
    }

    public void clearAction() {
        throw new NotImplementedException("");
//        game.getInteraction().clear();
    }

    public void sendEvent(String tag) {
        Application.notify(observer -> observer.onCustomEvent(tag, null));
    }

    public void sendEvent(String tag, Object object) {
        Application.notify(observer -> observer.onCustomEvent(tag, object));
    }
}
