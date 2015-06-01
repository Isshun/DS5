package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoItem extends BaseRightPanel {
    private UserItem _item;
    private ItemInfo _itemInfo;

    public PanelInfoItem(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_item.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        if (_item != null) {
            select(_item);
        } else if (_itemInfo != null) {
            select(_itemInfo);
        }
    }

    public void select(UserItem item) {
        _item = item;
        select(item.getInfo());

        if (isLoaded()) {
            ((TextView)findById("lb_durability")).setString("Durability: " + _item.getHealth());
            ((TextView)findById("lb_matter")).setString("Matter: " + _item.getMatter());
            ((TextView)findById("lb_pos")).setString("Pos: " + _item.getX() + "x" + _item.getY());
        }
    }

    public void select(ItemInfo info) {
        _itemInfo = info;

        if (isLoaded()) {
            ((TextView)findById("lb_name")).setString(_itemInfo.label);
        }
    }
}
