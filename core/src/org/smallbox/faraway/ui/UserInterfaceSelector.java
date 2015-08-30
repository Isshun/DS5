package org.smallbox.faraway.ui;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.module.world.AreaModule;
import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.area.AreaModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.ui.panel.PanelTooltip;
import org.smallbox.faraway.ui.panel.info.*;
import org.smallbox.faraway.ui.panel.right.PanelCharacter;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 28/06/2015.
 */
public class UserInterfaceSelector {
    public interface SelectStrategy {
        boolean onSelect(CharacterModel character, ParcelModel parcel, AreaModel area);
    }

    private final UserInterface _userInterface;
    private CharacterModel      _selectedCharacter;
    private ItemModel           _selectedItem;
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

            // Select area
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

    public boolean selectAt(int x, int y) {
        Game.getInstance().notify(GameObserver::onDeselect);

        ParcelModel parcel = Game.getWorldManager().getParcel(x, y);
        if (parcel != null) {
            CharacterModel character = Game.getCharacterManager().getCharacterAtPos(x, y);
            AreaModel area = ((AreaModule) Game.getInstance().getManager(AreaModule.class)).getArea(x, y);

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
        Game.getInstance().notify(GameObserver::onDeselect);

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                CharacterModel character = Game.getCharacterManager().getCharacterAtPos(x, y);
                if (character != null) {
                    select(character);
                    return true;
                }
            }
        }

        return false;
    }

    public void clean() {
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(false);
        }
        _selectedCharacter = null;
        if (_selectedItem != null) {
            _selectedItem.setSelected(false);
        }
        _selectedItem = null;
    }

    public void select(ItemInfo itemInfo) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_ITEM);
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
        Game.getInstance().notify(observer -> observer.onSelectCharacter(character));
    }

    public void select(AreaModel area, ParcelModel parcel) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_AREA);
        ((PanelInfoArea)_userInterface.getPanel(PanelInfoArea.class)).select(area, parcel);
    }

    public void select(ResourceModel resource) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_RESOURCE);
        ((PanelInfoResource)_userInterface.getPanel(PanelInfoResource.class)).select(resource);
        Game.getInstance().notify(observer -> observer.onSelectResource(resource));
        Game.getInstance().notify(observer -> observer.onSelectParcel(resource.getParcel()));
    }

    public void select(ItemModel item) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO);
        _userInterface.setMode(UserInterface.Mode.INFO_ITEM);
        _selectedItem = item;
        ((PanelInfoItem)_userInterface.getPanel(PanelInfoItem.class)).select(item);
        Game.getInstance().notify(observer -> observer.onSelectItem(item));
        Game.getInstance().notify(observer -> observer.onSelectParcel(item.getParcel()));
    }

    public void select(ConsumableModel consumable) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO);
        _userInterface.setMode(UserInterface.Mode.INFO_CONSUMABLE);
        ((PanelInfoConsumable)_userInterface.getPanel(PanelInfoConsumable.class)).select(consumable);
        Game.getInstance().notify(observer -> observer.onSelectConsumable(consumable));
        Game.getInstance().notify(observer -> observer.onSelectParcel(consumable.getParcel()));
    }

    public void select(StructureModel structure) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_STRUCTURE);
        ((PanelInfoStructure)_userInterface.getPanel(PanelInfoStructure.class)).select(structure);
        Game.getInstance().notify(observer -> observer.onSelectStructure(structure));
        Game.getInstance().notify(observer -> observer.onSelectParcel(structure.getParcel()));
    }

    public void select(ParcelModel parcel) {
        clean();
        _userInterface.setMode(UserInterface.Mode.INFO_PARCEL);
        ((PanelInfoParcel)_userInterface.getPanel(PanelInfoParcel.class)).select(parcel);
        Game.getInstance().notify(observer -> observer.onSelectParcel(parcel));
    }

}
