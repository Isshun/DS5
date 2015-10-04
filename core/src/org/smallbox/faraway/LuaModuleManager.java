package org.smallbox.faraway;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCrewModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.model.area.AreaModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.lua.extend.LuaExtendInterface;
import org.smallbox.faraway.lua.extend.LuaItemExtend;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaModuleManager implements GameObserver {
    private List<LuaEventListener>      _luaEventListeners = new ArrayList<>();
    private List<LuaRefreshListener>    _luaRefreshListeners = new ArrayList<>();
    private List<LuaLoadListener>       _luaLoadListeners = new ArrayList<>();

    private LuaCrewModel        _luaCrew;
    private LuaEventsModel      _luaEvents;
    private LuaExtendManager    _luaExtendManager;

    public void init() {
//        loadUI();

        _luaExtendManager = new LuaExtendManager();
        _luaExtendManager.addExtendFactory(new LuaUIExtend());
        _luaExtendManager.addExtendFactory(new LuaItemExtend());
        _luaExtendManager.addExtendFactory(new LuaCursorExtend());

        _luaCrew = new LuaCrewModel();
        _luaEvents = new LuaEventsModel();
    }

    private void loadUI() {
        UserInterface.getInstance()._views.clear();
        _luaEventListeners.clear();
        _luaLoadListeners.clear();
        _luaRefreshListeners.clear();

        // Load lua scripts
//        stream().filter(file -> file.getName().endsWith(".lua"))
        FileUtils.list("data/modules/").forEach(this::loadModule);

        _luaLoadListeners.forEach(LuaLoadListener::onLoad);

        if (UserInterface.getInstance().getSelector().getSelectedCharacter() != null) {
            Game.getInstance().notify(o -> o.onSelectCharacter(UserInterface.getInstance().getSelector().getSelectedCharacter()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedConsumable() != null) {
            Game.getInstance().notify(o -> o.onSelectConsumable(UserInterface.getInstance().getSelector().getSelectedConsumable()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedItem() != null) {
            Game.getInstance().notify(o -> o.onSelectItem(UserInterface.getInstance().getSelector().getSelectedItem()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedResource() != null) {
            Game.getInstance().notify(o -> o.onSelectResource(UserInterface.getInstance().getSelector().getSelectedResource()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedStructure() != null) {
            Game.getInstance().notify(o -> o.onSelectStructure(UserInterface.getInstance().getSelector().getSelectedStructure()));
        }
    }

    private void loadModule(File file) {
        Globals globals = JsePlatform.standardGlobals();
        globals.load("function main(g)\n game = g\n end", "main").call();
        globals.get("main").call(CoerceJavaToLua.coerce(new LuaGameModel(_luaCrew, _luaEvents, UserInterface.getInstance(), values -> {
            for (int i = 1; i <= values.length(); i++) {
                LuaValue value = values.get(i);
                for (LuaExtendInterface luaExtend: _luaExtendManager.getExtends()) {
                    if (luaExtend.accept(value.get("type").toString())) {
                        luaExtend.extend(this, globals, value);
                        break;
                    }
                }
            }
        })));

        FileUtils.listRecursively(file.getAbsolutePath()).stream().filter(f -> f.getName().endsWith(".lua")).forEach(f -> {
            try {
                globals.load(new FileReader(f), f.getName()).call();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (LuaError e) {
                e.printStackTrace();
            }
        });
    }

    public void update() {

    }

    public void addLuaRefreshListener(LuaRefreshListener luaRefreshListener) {
        _luaRefreshListeners.add(luaRefreshListener);
    }

    public void addLuaEventListener(LuaEventListener luaEventListener) {
        _luaEventListeners.add(luaEventListener);
    }

    public void addLuaLoadListener(LuaLoadListener luaLoadListener) {
        _luaLoadListeners.add(luaLoadListener);
    }

    private void broadcastToLuaModules(int eventId, Object data) {
        LuaValue value = CoerceJavaToLua.coerce(data);
        _luaEventListeners.forEach(listener -> listener.onEvent(eventId, value));
    }

    //    default void onAddCharacter(CharacterModel character){}
//    default void onAddStructure(StructureModel structure){}
//    default void onAddItem(ItemModel item){}
//    default void onAddConsumable(ConsumableModel consumable){}
//    default void onAddResource(ResourceModel resource) {}
//    default void onRemoveItem(ItemModel item){}
//    default void onRemoveConsumable(ConsumableModel consumable){}
//    default void onRemoveStructure(StructureModel structure){}
//    default void onRemoveResource(ResourceModel resource){}
//    default void onRefreshItem(ItemModel item) {}
//    default void onRefreshStructure(StructureModel structure) {}
//    default void onHourChange(int hour){}
//    default void onDayChange(int day) {}
//    default void onYearChange(int year) {}
//    //    default void onOpenQuest(QuestModel quest) {}
////    default void onCloseQuest(QuestModel quest) {}
    public void onSelectArea(AreaModel area) { broadcastToLuaModules(LuaEventsModel.on_area_selected, area); }
    public void onSelectCharacter(CharacterModel character) { broadcastToLuaModules(LuaEventsModel.on_character_selected, character); }
    public void onSelectParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_selected, parcel); }
    public void onSelectItem(ItemModel item) { broadcastToLuaModules(LuaEventsModel.on_item_selected, item); }
    public void onSelectResource(ResourceModel resource) { broadcastToLuaModules(LuaEventsModel.on_resource_selected, resource); }
    public void onSelectConsumable(ConsumableModel consumable) { broadcastToLuaModules(LuaEventsModel.on_consumable_selected, consumable); }
    public void onSelectStructure(StructureModel structure) { broadcastToLuaModules(LuaEventsModel.on_structure_selected, structure); }
    public void onDeselect() { broadcastToLuaModules(LuaEventsModel.on_deselect, null); }
    public void onReloadUI() {loadUI();}
    public void onRefreshUI() { _luaRefreshListeners.forEach(LuaRefreshListener::onRefresh); }
//    default void onStartGame() {}
//    default void onLog(String tag, String message) {}
//    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
//    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {}

    public void onJobCreate(BaseJobModel job) { broadcastToLuaModules(LuaEventsModel.on_job_create, job);}

}
