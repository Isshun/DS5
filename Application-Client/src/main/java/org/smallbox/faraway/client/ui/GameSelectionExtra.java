package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.plant.model.PlantItem;

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
        ApplicationClient.notify(GameClientObserver::onDeselect);

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
//    private UsableItem           _selectedItem;
    private ItemInfo            _selectedRock;
    private PlantItem _selectedPlant;
    private StructureItem _selectedStructure;
    private ParcelModel         _selectedParcel;
    private AreaModel           _selectedArea;

    public CharacterModel       getSelectedCharacter() { return _selectedCharacter; }
    public PlantItem getSelectedResource() { return _selectedPlant; }
//    public UsableItem            getSelectedItem() { return _selectedItem; }
    public StructureItem getSelectedStructure() { return _selectedStructure; }
    public ParcelModel          getSelectedParcel() { return _selectedParcel; }
    public AreaModel            getSelectedArea() { return _selectedArea; }
}
