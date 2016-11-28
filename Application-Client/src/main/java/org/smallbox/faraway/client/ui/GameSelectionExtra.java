package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.AreaModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.PlantModel;
import org.smallbox.faraway.core.module.world.model.StructureModel;

/**
 * Created by Alex on 28/06/2015.
 */
public class GameSelectionExtra {
    private ParcelModel _lastMoveParcel;

    public boolean isClear() {
        return _selectedArea == null
//                && _selectedItem == null
                && _selectedPlant == null
                && _selectedStructure == null
                && _selectedParcel == null
                && _selectedCharacter == null;
    }

    public void clear() {
        Application.notify(GameObserver::onDeselect);

        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(false);
        }
        _selectedCharacter = null;

//        if (_selectedItem != null) {
//            _selectedItem.setSelected(false);
//        }
//        _selectedItem = null;

        _selectedPlant = null;
        _selectedParcel = null;
        _selectedCharacter = null;
        _selectedArea = null;
        _selectedStructure = null;
    }

    public interface SelectStrategy {
        boolean onSelect(CharacterModel character, ParcelModel parcel, AreaModel area);
    }

    private CharacterModel      _selectedCharacter;
//    private ItemModel           _selectedItem;
    private ItemInfo            _selectedRock;
    private PlantModel          _selectedPlant;
    private StructureModel      _selectedStructure;
    private ParcelModel         _selectedParcel;
    private AreaModel           _selectedArea;

    public CharacterModel       getSelectedCharacter() { return _selectedCharacter; }
    public PlantModel           getSelectedResource() { return _selectedPlant; }
//    public ItemModel            getSelectedItem() { return _selectedItem; }
    public StructureModel       getSelectedStructure() { return _selectedStructure; }
    public ParcelModel          getSelectedParcel() { return _selectedParcel; }
    public AreaModel            getSelectedArea() { return _selectedArea; }
}
