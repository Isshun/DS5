package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.input.GameClientObserver;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.structure.StructureItem;
import org.smallbox.faraway.game.world.Parcel;

public class GameSelectionExtra {
    private Parcel _lastMoveParcel;

    public boolean isClear() {
        return _selectedArea == null
//                && _selectedItem == null
                && _selectedPlant == null
                && _selectedStructure == null
                && _selectedParcel == null
                && _selectedCharacter == null;
    }

    public void clear() {
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
        boolean onSelect(CharacterModel character, Parcel parcel, AreaModel area);
    }

    private CharacterModel      _selectedCharacter;
//    private UsableItem           _selectedItem;
    private ItemInfo            _selectedRock;
    private PlantItem _selectedPlant;
    private StructureItem _selectedStructure;
    private Parcel _selectedParcel;
    private AreaModel           _selectedArea;

    public CharacterModel       getSelectedCharacter() { return _selectedCharacter; }
    public PlantItem getSelectedResource() { return _selectedPlant; }
//    public UsableItem            getSelectedItem() { return _selectedItem; }
    public StructureItem getSelectedStructure() { return _selectedStructure; }
    public Parcel getSelectedParcel() { return _selectedParcel; }
    public AreaModel            getSelectedArea() { return _selectedArea; }
}
