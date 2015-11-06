package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.ToolTips;
import org.smallbox.faraway.core.game.module.area.AreaModule;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Constant;

/**
 * Created by Alex on 28/06/2015.
 */
public class UserInterfaceSelector {
    private ParcelModel _lastMoveParcel;

    public boolean isClear() {
        return _selectedArea == null
                && _selectedItem == null
                && _selectedResource == null
                && _selectedStructure == null
                && _selectedParcel == null
                && _selectedCharacter == null
                && _selectedConsumable == null;
    }

    public interface SelectStrategy {
        boolean onSelect(CharacterModel character, ParcelModel parcel, AreaModel area);
    }

    private final UserInterface _userInterface;
    private CharacterModel      _selectedCharacter;
    private ItemModel           _selectedItem;
    private ResourceModel       _selectedResource;
    private StructureModel      _selectedStructure;
    private ParcelModel         _selectedParcel;
    private AreaModel           _selectedArea;
    private NetworkObjectModel  _selectedNetwork;
    private ConsumableModel     _selectedConsumable;
    private ParcelModel         _lastSelectedParcel;
    private int                 _lastSelectedIndex;

    private SelectStrategy[]    SELECTORS = new SelectStrategy[] {

            // Select characters
            (character, parcel, area) -> {
                if (character != null && character != _selectedCharacter) {
                    select(character);
                    return true;
                }
                return false;
            },

            // Select item
            (character, parcel, area) -> {
                int x = parcel.x;
                int y = parcel.y;
                for (int x2 = 0; x2 < Constant.ITEM_MAX_WIDTH; x2++) {
                    for (int y2 = 0; y2 < Constant.ITEM_MAX_HEIGHT; y2++) {
                        ItemModel item = WorldHelper.getItem(x - x2, y - y2);
                        if (item != null && item.getWidth() > x2 && item.getHeight() > y2) {
                            select(item);
                            return true;
                        }
                    }
                }
                return false;
            },

            // Select consumable
            (character, parcel, area) -> {
                if (parcel != null && parcel.getConsumable() != null) {
                    select(parcel.getConsumable());
                    return true;
                }
                return false;
            },

            // Select resource
            (character, parcel, area) -> {
                if (parcel != null && parcel.getResource() != null) {
                    select(parcel.getResource());
                    return true;
                }
                return false;
            },

            // Select model
            (character, parcel, area) -> {
                if (area != null && !area.isHome()) {
                    select(area, parcel);
                    return true;
                }
                return false;
            },

            // Select structure
            (character, parcel, area) -> {
                if (parcel != null && parcel.getStructure() != null) {
                    select(parcel.getStructure());
                    return true;
                }
                return false;
            },

            // Select network
            (character, parcel, area) -> {
                if (parcel != null && parcel.getNetworkObjects() != null && !parcel.getNetworkObjects().isEmpty()) {
                    select(parcel.getNetworkObjects().get(0));
                    return true;
                }
                return false;
            },

            // Select home
            (character, parcel, area) -> {
                if (area != null && area.isHome()) {
                    select(area, parcel);
                    return true;
                }
                return false;
            },

    };

    public UserInterfaceSelector(UserInterface userInterface) {
        _userInterface = userInterface;
    }

    public CharacterModel       getSelectedCharacter() { return _selectedCharacter; }
    public ResourceModel        getSelectedResource() { return _selectedResource; }
    public ItemModel            getSelectedItem() { return _selectedItem; }
    public StructureModel       getSelectedStructure() { return _selectedStructure; }
    public ParcelModel          getSelectedParcel() { return _selectedParcel; }
    public AreaModel            getSelectedArea() { return _selectedArea; }
    public ConsumableModel      getSelectedConsumable() { return _selectedConsumable; }

    public boolean selectAt(int x, int y) {
        Application.getInstance().notify(GameObserver::onDeselect);

        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
        if (parcel != null) {
            CharacterModel character = ModuleHelper.getCharacterModule().getCharacterAtPos(x, y);
            AreaModel area = ((AreaModule) ModuleManager.getInstance().getModule(AreaModule.class)).getArea(x, y);

            _lastSelectedIndex = _lastSelectedParcel == parcel ? _lastSelectedIndex + 1 : 0;
            _lastSelectedParcel = parcel;

            // Select best items on parcel
            for (int i = 0; i < SELECTORS.length; i++) {
                if (SELECTORS[(i + _lastSelectedIndex) % SELECTORS.length].onSelect(character, parcel, area)) {
                    _lastSelectedIndex = (i + _lastSelectedIndex) % SELECTORS.length;
                    return true;
                }
            }

            // Select parcel
            select(parcel);
            return true;
        }

        return false;
    }

    public boolean selectAt(int fromX, int fromY, int toX, int toY) {
        Application.getInstance().notify(GameObserver::onDeselect);

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                CharacterModel character = ModuleHelper.getCharacterModule().getCharacterAtPos(x, y);
                if (character != null) {
                    select(character);
                    return true;
                }
            }
        }

        return false;
    }

    public void moveAt(int x, int y) {
        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
        if (_lastMoveParcel != parcel) {
            _lastMoveParcel = parcel;
            Application.getInstance().notify(observer -> observer.onOverParcel(parcel));
        }
    }

    public void clean() {
        Application.getInstance().notify(GameObserver::onDeselect);
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(false);
        }
        _selectedCharacter = null;
        if (_selectedItem != null) {
            _selectedItem.setSelected(false);
        }
        _selectedItem = null;
        _selectedResource = null;
        _selectedParcel = null;
        _selectedCharacter = null;
        _selectedArea = null;
        _selectedStructure = null;
        _selectedNetwork = null;
    }

    public void select(ItemInfo itemInfo) {
        clean();
//        _userInterface.setMode(UserInterface.Mode.INFO_ITEM);
//        ((PanelInfoItem)_userInterface.getPanel(PanelInfoItem.class)).select(item);
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
//        _userInterface.setMode(UserInterface.Mode.TOOLTIP);
//        ((PanelTooltip)_userInterface.getPanel(PanelTooltip.class)).select(tooltip);
    }

    public void select(ReceiptGroupInfo receipt) {
        clean();
        Application.getInstance().notify(observer -> observer.onSelectReceipt(receipt));
    }

    public void select(CharacterModel character) {
        clean();
        _selectedCharacter = character;
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(true);
        }
        Application.getInstance().notify(observer -> observer.onSelectCharacter(character));
    }

    public void select(AreaModel area, ParcelModel parcel) {
        clean();
        _selectedArea = area;
        Application.getInstance().notify(observer -> observer.onSelectArea(area));
    }

    public void select(ResourceModel resource) {
        clean();
        _selectedResource = resource;
        Application.getInstance().notify(observer -> observer.onSelectResource(resource));
        Application.getInstance().notify(observer -> observer.onSelectParcel(resource.getParcel()));
    }

    public void select(ItemModel item) {
        clean();
        _selectedItem = item;
        Application.getInstance().notify(observer -> observer.onSelectItem(item));
        Application.getInstance().notify(observer -> observer.onSelectParcel(item.getParcel()));
    }

    public void select(ConsumableModel consumable) {
        clean();
        _selectedConsumable = consumable;
        Application.getInstance().notify(observer -> observer.onSelectConsumable(consumable));
        Application.getInstance().notify(observer -> observer.onSelectParcel(consumable.getParcel()));
    }

    public void select(NetworkObjectModel network) {
        clean();
        _selectedNetwork = network;
        Application.getInstance().notify(observer -> observer.onSelectNetwork(network));
        Application.getInstance().notify(observer -> observer.onSelectParcel(network.getParcel()));
    }

    public void select(StructureModel structure) {
        clean();
        _selectedStructure = structure;
        Application.getInstance().notify(observer -> observer.onSelectStructure(structure));
        Application.getInstance().notify(observer -> observer.onSelectParcel(structure.getParcel()));
    }

    public void select(ParcelModel parcel) {
        clean();
        _selectedParcel = parcel;
        Application.getInstance().notify(observer -> observer.onSelectParcel(parcel));
    }

}
