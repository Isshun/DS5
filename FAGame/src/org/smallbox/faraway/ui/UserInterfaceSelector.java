package org.smallbox.faraway.ui;

import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.panel.PanelRoom;
import org.smallbox.faraway.ui.panel.PanelTooltip;
import org.smallbox.faraway.ui.panel.info.*;
import org.smallbox.faraway.ui.panel.right.PanelCharacter;

/**
 * Created by Alex on 28/06/2015.
 */
public class UserInterfaceSelector {
    private final UserInterface _userInterface;
    private ToolTips.ToolTip    _selectedTooltip;
    private CharacterModel      _selectedCharacter;
    private ItemModel           _selectedItem;
    private StructureModel      _selectedStructure;
    private ResourceModel       _selectedResource;
    private ParcelModel         _selectedParcel;
    private RoomModel           _selectedRoom;
    private AreaModel           _selectedArea;
    private ItemInfo            _selectedItemInfo;
    private ConsumableModel     _selectedConsumable;

    public UserInterfaceSelector(UserInterface userInterface) {
        _userInterface = userInterface;
    }

    public ToolTips.ToolTip     getSelectedTooltip() { return _selectedTooltip; }
    public CharacterModel       getSelectedCharacter() { return _selectedCharacter; }
    public ParcelModel          getSelectedArea() { return _selectedParcel; }
    public ItemModel            getSelectedItem() { return _selectedItem; }
    public ResourceModel        getSelectedResource() { return _selectedResource; }
    public StructureModel       getSelectedStructure() { return _selectedStructure; }
    public ItemInfo			    getSelectedItemInfo() { return _selectedItemInfo; }
    public RoomModel            getSelectedRoom() { return _selectedRoom; }

    public void clean() {
        _selectedParcel = null;
        _selectedStructure = null;
        _selectedItemInfo = null;
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(false);
        }
        _selectedCharacter = null;
        if (_selectedItem != null) {
            _selectedItem.setSelected(false);
        }
        _selectedItem = null;
        _selectedResource = null;
        _selectedTooltip = null;
    }

    public void select(ItemInfo itemInfo) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_ITEM);
        _selectedItemInfo = itemInfo;
        ((PanelInfoItem)_userInterface.getPanel(PanelInfoItem.class)).select(itemInfo);
    }

    public void select(MapObjectModel item) {
        if (item.isUserItem()) {
            select((ItemModel)item);
        }
        else if (item.isStructure()) {
            select((StructureModel)item);
        }
    }

    public void select(ToolTips.ToolTip tooltip) {
        _selectedTooltip = tooltip;
        _userInterface.setMode(UserInterface.Mode.TOOLTIP);
        ((PanelTooltip)_userInterface.getPanel(PanelTooltip.class)).select(tooltip);
    }

    public void select(CharacterModel character) {
        clean();
        _userInterface.setMode(UserInterface.Mode.CHARACTER);
        _selectedCharacter = character;
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(true);
        }
        ((PanelCharacter)_userInterface.getPanel(PanelCharacter.class)).select(character);
    }

    public void select(RoomModel room) {
        clean();
        _userInterface.setMode(UserInterface.Mode.ROOM);
        _selectedRoom = room;
        ((PanelRoom)_userInterface.getPanel(PanelRoom.class)).select(room);
    }

    public void select(AreaModel area) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_AREA);
        _selectedArea = area;
        ((PanelInfoArea)_userInterface.getPanel(PanelInfoArea.class)).select(area);
    }

    public void select(ResourceModel resource) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_RESOURCE);
        _selectedResource = resource;
        ((PanelInfoResource)_userInterface.getPanel(PanelInfoResource.class)).select(resource);
    }

    public void select(ItemModel item) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO);
        _userInterface.setMode(UserInterface.Mode.INFO_ITEM);
        _selectedItem = item;
        ((PanelInfoItem)_userInterface.getPanel(PanelInfoItem.class)).select(item);
    }

    public void select(ConsumableModel consumable) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO);
        _userInterface.setMode(UserInterface.Mode.INFO_CONSUMABLE);
        _selectedConsumable = consumable;
        ((PanelInfoConsumable)_userInterface.getPanel(PanelInfoConsumable.class)).select(consumable);
    }

    public void select(StructureModel structure) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_STRUCTURE);
        _selectedStructure = structure;
        ((PanelInfoStructure)_userInterface.getPanel(PanelInfoStructure.class)).select(structure);
    }

    public void select(ParcelModel area) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_PARCEL);
        _selectedParcel = area;
        ((PanelInfoParcel)_userInterface.getPanel(PanelInfoParcel.class)).select(area);
    }

}
