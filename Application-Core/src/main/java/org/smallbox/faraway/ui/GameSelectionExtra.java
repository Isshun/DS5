package org.smallbox.faraway.ui;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 28/06/2015.
 */
public class GameSelectionExtra {
    private ParcelModel _lastMoveParcel;

    public boolean isClear() {
        return _selectedArea == null
                && _selectedItem == null
                && _selectedPlant == null
                && _selectedStructure == null
                && _selectedParcel == null
                && _selectedCharacter == null;
    }

    public interface SelectStrategy {
        boolean onSelect(CharacterModel character, ParcelModel parcel, AreaModel area);
    }

    private CharacterModel      _selectedCharacter;
    private ItemModel           _selectedItem;
    private ItemInfo            _selectedRock;
    private PlantModel          _selectedPlant;
    private StructureModel      _selectedStructure;
    private ParcelModel         _selectedParcel;
    private AreaModel           _selectedArea;

    public CharacterModel       getSelectedCharacter() { return _selectedCharacter; }
    public PlantModel           getSelectedResource() { return _selectedPlant; }
    public ItemModel            getSelectedItem() { return _selectedItem; }
    public StructureModel       getSelectedStructure() { return _selectedStructure; }
    public ParcelModel          getSelectedParcel() { return _selectedParcel; }
    public AreaModel            getSelectedArea() { return _selectedArea; }

    public boolean selectAt(int x, int y, int z) {
        throw new NotImplementedException("");

//        Application.getInstance().notify(GameObserver::onDeselect);
//
//        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y, z);
//        if (parcel != null) {
//            for (GameModule module: ModuleManager.getInstance().getGameModules()) {
//                try {
//                    if (module.onSelectParcel(parcel)) {
//                        return true;
//                    }
//                } catch (Exception e) {
//                     //TODO
//                    Log.error(e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return false;
    }

    public boolean selectAt(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        throw new NotImplementedException("");

//        Application.getInstance().notify(GameObserver::onDeselect);
//
//        for (int x = fromX; x <= toX; x++) {
//            for (int y = fromY; y <= toY; y++) {
//                for (int z = fromZ; z <= toZ; z++) {
//                    CharacterModel character = ModuleHelper.getCharacterModule().getCharacterAtPos(x, y, z);
//                    if (character != null) {
//                        for (GameModule module: ModuleManager.getInstance().getGameModules()) {
//                            if (module.onSelectCharacter(character)) {
//                                return true;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return false;
    }

    public void moveAt(int x, int y, int z) {
        throw new NotImplementedException("");

//        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y, z);
//        if (_lastMoveParcel != parcel) {
//            _lastMoveParcel = parcel;
//            Application.getInstance().notify(observer -> observer.onOverParcel(parcel));
//        }
    }

    public void clear() {
        Application.getInstance().notify(GameObserver::onDeselect);
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(false);
        }
        _selectedCharacter = null;
        if (_selectedItem != null) {
            _selectedItem.setSelected(false);
        }
        _selectedItem = null;
        _selectedPlant = null;
        _selectedParcel = null;
        _selectedCharacter = null;
        _selectedArea = null;
        _selectedStructure = null;
    }
}
