//package org.smallbox.faraway.common.lua.luaModel;
//
//import com.badlogic.gdx.Game;
//import org.apache.commons.lang3.NotImplementedException;
//import org.smallbox.faraway.common.ModuleBase;
//import org.smallbox.faraway.common.lua.LuaModule;
//import org.smallbox.faraway.common.modelInfo.ItemInfo;
//
//import java.util.Collection;
//import java.util.Optional;
//
///**
// * Created by Alex on 26/09/2015.
// */
//public class LuaApplicationModel {
//    public long                     tick;
//    public int                      day;
//    public int                      hour;
//    public int                      year;
//    public LuaCrewModel             crew;
//    public LuaEventsModel           events;
//    public Game game;
//    public Config                   config = new Config();
//    public ApplicationInfo          info = new ApplicationInfo();
//    public Collection<LuaModule>    luaModules;
//    public Collection<ModuleBase>   modules;
//    public Collection<ModuleBase>   moduleThirds;
//
//    public LuaApplicationModel(LuaCrewModel luaCrew, LuaEventsModel luaEvents) {
//        crew = luaCrew;
//        events = luaEvents;
//        luaModules = Application.luaModuleManager.getModules();
//        modules = Application.moduleManager.getModules();
//        moduleThirds = Application.moduleManager.getModulesThird();
//    }
//
//    public void update() {
//        this.game = Application.gameManager.getGame();
//        this.tick = game.getTick();
//        this.hour = game.getTime().getHour();
//        this.day = game.getTime().getDay();
//        this.year = game.getTime().getYear();
//    }
//
//    public ModuleBase getModule(String name) {
//        Optional<ModuleBase> moduleOptional = this.modules.stream().filter(module -> module.getInfo().name.equals(name)).findFirst();
//        return moduleOptional.isPresent() ? moduleOptional.get() : null;
//    }
//
//    public void setDisplay(String display) {
//        game.setDisplay(display, true);
//    }
//
//    public void toggleDisplay(String display) {
//        game.toggleDisplay(display);
//    }
//
//    public void setPlan(String plan) {
//        throw new NotImplementedException("");
////        game.getInteraction().set(GameActionExtra.Action.SET_PLAN, plan);
//    }
//
//    public void destroy(MapObjectModel object) {
//        throw new NotImplementedException("");
////        game.getInteraction().planDestroy(object.getParcel());
//    }
//
//    public void cancel(MapObjectModel object) {
//        Application.notify(observer -> observer.onCancelJobs(object.getParcel(), object));
//    }
//
//    public void setSpeed(int speed) {
//        if (Application.gameManager.isLoaded()) {
//            game.setSpeed(speed);
//        }
//    }
//
//    public void setArea(String area) {
//        throw new NotImplementedException("");
////        game.getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.valueOf(area.toUpperCase()));
//    }
//
//    public void removeArea(String area) {
//        throw new NotImplementedException("");
////        game.getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.valueOf(area.toUpperCase()));
//    }
//
//    public void setBuild(ItemInfo itemInfo) {
//        throw new NotImplementedException("");
////        Log.info("Set build from lua: " + itemInfo.name);
////        game.getInteraction().set(GameActionExtra.Action.BUILD_ITEM, itemInfo);
//    }
//
//    public void exit() {
//        Application.setRunning(false);
//    }
//
//    public void clearAction() {
//        throw new NotImplementedException("");
////        game.getInteraction().clear();
//    }
//
//    public void sendEvent(String tag) {
//        Application.notify(observer -> observer.onCustomEvent(tag, null));
//    }
//
//    public void sendEvent(String tag, Object object) {
//        Application.notify(observer -> observer.onCustomEvent(tag, object));
//    }
//}
