package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.GameTimer;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

import java.util.stream.Collectors;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoItem extends BaseInfoRightPanel {
    private ItemModel _item;
    private ItemInfo    _itemInfo;
    private ViewFactory _viewFactory;
    private FrameLayout _frameCraft;
    private FrameLayout _frameCraftEntries;
    private FrameLayout _menuAddCraft;
    private FrameLayout _menuAddCraftEntries;
    private View        _btAddCraft;
    private FrameLayout _frameTmp;

    public PanelInfoItem(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_item.yml");
    }
    
    @Override
    public void onCreate(ViewFactory factory) {
        _viewFactory = factory;
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        super.onLayoutLoaded(layout);

        _frameTmp = (FrameLayout) findById("frame_tmp");
        _frameTmp.setVisible(false);
        _frameCraft = (FrameLayout) findById("frame_craft");
        _frameCraft.setVisible(false);
        _frameCraftEntries = (FrameLayout) findById("frame_craft_entries");
        _menuAddCraft = (FrameLayout) findById("menu_add_craft");
        _menuAddCraft.setVisible(false);
        _menuAddCraftEntries = (FrameLayout) findById("menu_add_craft_entries");
        _btAddCraft = findById("bt_add_craft");
        _btAddCraft.setOnClickListener(view -> _menuAddCraft.setVisible(true));

        if (_item != null) {
            select(_item);
        } else if (_itemInfo != null) {
            select(_itemInfo);
        }
    }

    @Override
    protected void onRefresh(int update) {
        if (_item != null && _item.needRefresh()) {
            select(_item);
        }
    }

    public void select(ItemModel item) {
        super.select(item.getParcel());

        _item = item;
        _itemInfo = item.getInfo();
        select(item.getInfo());

        if (isLoaded()) {
            ((TextView)findById("lb_id")).setString("(" + _item.getId() + ")");
            ((TextView)findById("lb_durability")).setString("Durability: " + _item.getHealth());
            ((TextView)findById("lb_matter")).setString("Matter: " + _item.getMatter());
            ((TextView)findById("lb_pos")).setString("Pos: " + _item.getX() + "x" + _item.getY());
            ((TextView)findById("lb_components")).setString("Components: " + String.join(", ", _item.getComponents().stream().map(ConsumableModel::getFullLabel).collect(Collectors.toList())));
            ((TextView)findById("lb_crafts")).setString("Crafts: " + String.join(", ", _item.getCrafts().stream().map(ConsumableModel::getFullLabel).collect(Collectors.toList())));
        }

        // Temperature
        if (_itemInfo.hasTemperatureEffect()) {
            _frameTmp.setVisible(true);
            ((TextView)findById("lb_tmp_target")).setString("Targeted: " + _item.getTargetTemperature());
            ((TextView)findById("lb_potency")).setString("Potency: " + _item.getInfo().effects.heatPotency);
            findById("bt_tmp_add").setOnClickListener(view -> _item.setTargetTemperature(_item.getTargetTemperature() + 1));
            findById("bt_tmp_sub").setOnClickListener(view -> _item.setTargetTemperature(_item.getTargetTemperature() - 1));
        } else {
            _frameTmp.setVisible(false);
        }

        // Craft actions
        if (_itemInfo.hasCraftAction()) {
            _frameCraft.setVisible(true);
            _frameCraftEntries.removeAllViews();
            _menuAddCraftEntries.removeAllViews();

            // Create menu
            int index = 0;
            for (ItemInfo.ItemInfoAction action: _itemInfo.actions) {
                addActionMenuEntry(action, index++);
            }

            // Create actions list
            index = 0;
            if (_item.hasJobs()) {
                for (BaseJobModel job : _item.getJobs()) {
                    addJobListEntry(job, index++);
                }
            }
        } else {
            _frameCraft.setVisible(false);
        }
    }

    public void select(ItemInfo info) {
        _itemInfo = info;

        if (isLoaded()) {
            ((TextView)findById("lb_name")).setString(_itemInfo.label);
        }
    }

    private void addJobListEntry(BaseJobModel job, int index) {
        _viewFactory.load("data/ui/panels/info_item_craft_entry.yml", view -> {
            view.findById("bt_suspend");
            view.findById("bt_cancel").setOnClickListener(v -> JobManager.getInstance().removeJob(job));
            ((TextView)view.findById("lb_label")).setString(job.getLabel());
            ((TextView)view.findById("lb_ingredient")).setString(job.getIngredient() != null ? job.getIngredient().getLabel() + " (" + job.getIngredient().getQuantity() + ")" : "no components");
            ((TextView)view.findById("lb_progress")).setString(job.getProgressPercent() + "%");

            TextView lbCraftCount = (TextView)view.findById("lb_count");
            lbCraftCount.setString(job.getTotalCount() == Integer.MAX_VALUE ? "xx" : "x" + job.getCount());
            lbCraftCount.setOnClickListener(v -> {
                job.setTotalCount(job.getTotalCount() == Integer.MAX_VALUE ? 1 : Integer.MAX_VALUE);
            });
            lbCraftCount.setOnRightClickListener(v -> {
                job.setTotalCount(job.getTotalCount() == Integer.MAX_VALUE ? 1 : Integer.MAX_VALUE);
            });

            view.setPosition(0, 40 + index * 80);
            _frameCraftEntries.addView(view);
//            _frameCraftEntries.resetAllPos();
        });
    }

    private void addActionMenuEntry(ItemInfo.ItemInfoAction action, int index) {
        TextView lbCraft = _viewFactory.createTextView();
        lbCraft.setString(action.label);
        lbCraft.setCharacterSize(14);
        lbCraft.setPosition(0, 0 + 28 * index);
        lbCraft.setPadding(0, 8);
        lbCraft.setSize(220, 28);
        lbCraft.setBackgroundColor(new Color(0x1d5560));
        lbCraft.setAlign(Align.CENTER_VERTICAL);
        lbCraft.setOnClickListener(view -> {
            JobManager.getInstance().addJob(_item, action);
        });
        _menuAddCraftEntries.addView(lbCraft);
    }

    @Override
    public boolean onMouseEvent(GameTimer timer, GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        if (action == GameEventListener.Action.RELEASED && _menuAddCraft.isVisible()) {
            _menuAddCraft.setVisible(false);

            // Click menu entry
            for (View view : _menuAddCraftEntries.getViews()) {
                if (view.getRect().contains(x, y)) {
                    view.onClick();
                    return true;
                }
            }

            return true;
        }

        return false;
    }

}
