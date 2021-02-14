package org.smallbox.faraway.core.lua.luaModel;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.lua.ServerLuaModuleManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.module.ModuleBase;
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
    @Inject private ApplicationConfig applicationConfig;
    @Inject private GameManager gameManager;
    @Inject private GameTime gameTime;
    @Inject public Game game;

    public int screen_width;
    public int screen_height;
    public Collection<LuaModule> luaModules;
    public Collection<ModuleBase> modules;
    public Collection<ModuleBase> moduleThirds;

    @OnInit
    public void init() {
        luaModules = serverLuaModuleManager.getModules();
        screen_width = applicationConfig.getResolutionWidth();
        screen_height = applicationConfig.getResolutionHeight();
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
