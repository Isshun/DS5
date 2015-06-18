package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoConsumable extends BaseRightPanel {
    private ConsumableModel _consumable;
    private ItemInfo    _itemInfo;

    public PanelInfoConsumable(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_consumable.yml");
    }
    
    @Override
    public void onCreate(ViewFactory factory) {
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        if (_consumable != null) {
            select(_consumable);
        } else if (_itemInfo != null) {
            select(_itemInfo);
        }
    }

    @Override
    protected void onRefresh(int update) {
        if (_consumable != null && _consumable.needRefresh()) {
            select(_consumable);
        }
    }

    public void select(ConsumableModel consumable) {
        _consumable = consumable;
        _itemInfo = consumable.getInfo();
        select(consumable.getInfo());

        if (isLoaded()) {
            ((TextView)findById("lb_id")).setString("(" + _consumable.getId() + ")");
            ((TextView)findById("lb_durability")).setString("Durability: " + _consumable.getHealth());
            ((TextView)findById("lb_matter")).setString("Matter: " + _consumable.getMatter());
            ((TextView)findById("lb_pos")).setString("Pos: " + _consumable.getX() + "x" + _consumable.getY());
            ((TextView)findById("lb_quantity")).setString("Quantity: %d", _consumable.getQuantity());
        }
    }

    public void select(ItemInfo info) {
        _itemInfo = info;

        if (isLoaded()) {
            ((TextView)findById("lb_name")).setString(_itemInfo.name);
            ((TextView)findById("lb_label")).setString(_itemInfo.label);
        }
    }


}
