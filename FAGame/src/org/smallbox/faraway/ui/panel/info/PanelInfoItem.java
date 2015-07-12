package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.JobHelper;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobCraft;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.faraway.ui.engine.ViewFactory;

import java.util.stream.Collectors;

import static org.smallbox.faraway.game.model.ReceiptModel.*;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoItem extends BaseInfoRightPanel {
    private ItemModel   _item;
    private ItemInfo    _itemInfo;
    private ViewFactory _viewFactory;
    private FrameLayout _frameCraft;
    private FrameLayout _frameCraftEntries;
    private FrameLayout _menuAddCraft;
    private FrameLayout _menuAddCraftEntries;
    private View        _btAddCraft;
    private FrameLayout _frameTmp;
    private FrameLayout _frameOwner;

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
        _frameOwner = (FrameLayout) findById("frame_owner");
        _frameOwner.setVisible(false);
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

        refreshDebug(_item);
    }

    private void refreshDebug(ItemModel item) {
        FrameLayout frame = (FrameLayout) findById("item_debug");
        frame.removeAllViews();

        int index = 0;
        if (item.getJobs() != null && !item.getJobs().isEmpty() && item.getJobs().get(0) instanceof JobCraft && ((JobCraft)item.getJobs().get(0)).getReceipt() != null) {
            ReceiptModel receipt = ((JobCraft)item.getJobs().get(0)).getReceipt();
            for (OrderModel order: receipt.getOrders()) {
                UILabel lbDebug = ViewFactory.getInstance().createTextView();
                lbDebug.setCharacterSize(14);
                lbDebug.setString((order.status == OrderModel.Status.STORED ? "[x]" : order.status == OrderModel.Status.CARRY ? "[c]" : "[ ]") + order.consumable.getInfo().name + " -> " + order.quantity);
                lbDebug.setPosition(0, index++ * 20);
                frame.addView(lbDebug);
            }
        }
    }

    public void select(ItemModel item) {
        super.select(item.getParcel());

        _item = item;
        _itemInfo = item.getInfo();
        select(item.getInfo());

        if (isLoaded()) {
            ((UILabel)findById("lb_id")).setString("(" + _item.getId() + ")");
            ((UILabel)findById("lb_durability")).setString("Durability: " + _item.getHealth());
            ((UILabel)findById("lb_matter")).setString("Matter: " + _item.getMatter());
            ((UILabel)findById("lb_components")).setString("Components: " + String.join(", ", _item.getComponents().stream().map(ConsumableModel::getFullLabel).collect(Collectors.toList())));

            JobCraft jobCraft = (JobCraft)(_item.getJobs() != null && !_item.getJobs().isEmpty() && _item.getJobs().get(0) instanceof JobCraft ? _item.getJobs().get(0) : null);
            if (jobCraft != null && jobCraft.isRunning() && jobCraft.getReceipt() != null) {
                ((UILabel)findById("lb_crafts")).setString("Crafts: " + String.join(", ", jobCraft.getReceipt().getProductsInfo().stream().map(product -> product.itemInfo.label).collect(Collectors.toList())));
                ((UILabel)findById("lb_users")).setString("User: " + jobCraft.getCharacter().getName());
            } else {
                ((UILabel)findById("lb_crafts")).setString("Crafts: none");
                ((UILabel)findById("lb_users")).setString("Users: none");
            }
        }

        // Temperature
        if (_itemInfo.hasTemperatureEffect()) {
            _frameTmp.setVisible(true);
            ((UILabel)findById("lb_tmp_target")).setString("Targeted: " + _item.getTargetTemperature());
            ((UILabel)findById("lb_potency")).setString("Potency: " + _item.getPotencyUse() + "/" + _item.getInfo().effects.heatPotency);
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

        // Bed item
        if (_itemInfo.isBed) {
            _frameOwner.setVisible(true);
            findById("bt_owner_common").setOnClickListener(view -> setOwner(0));
            findById("bt_owner_selected").setOnClickListener(view -> setOwner(1));
            findById("bt_owner_jail").setOnClickListener(view -> setOwner(2));
        } else {
            _frameOwner.setVisible(false);
        }

        findById("bt_destroy").setOnClickListener(view -> JobHelper.addDumpJob(item));
    }

    private void setOwner(int index) {
        ((UILabel)findById("bt_owner_common")).setString(index == 0 ? "> Common" : "  Common");
        ((UILabel)findById("bt_owner_selected")).setString(index == 1 ? "> Personal" : "  Personal");
        ((UILabel)findById("bt_owner_jail")).setString(index == 2 ? "> Jail" : "  Jail");
    }

    public void select(ItemInfo info) {
        _itemInfo = info;

        if (isLoaded()) {
            ((UILabel)findById("lb_name")).setString(_itemInfo.label);
        }
    }

    private void addJobListEntry(BaseJobModel job, int index) {
        _viewFactory.load("data/ui/panels/info_item_craft_entry.yml", view -> {
            view.findById("bt_suspend");
            view.findById("bt_cancel").setOnClickListener(v -> JobManager.getInstance().removeJob(job));
            ((UILabel)view.findById("lb_label")).setString(job.getLabel());
            ((UILabel)view.findById("lb_ingredient")).setString(job.getIngredient() != null ? job.getIngredient().getLabel() + " (" + job.getIngredient().getQuantity() + ")" : "no components");

            if (job.getProgressPercent() > 0) {
                ((UILabel) view.findById("lb_progress")).setString(job.getMessage() + ": " + job.getProgressPercent() + "%");
            } else {
                ((UILabel) view.findById("lb_progress")).setString(job.getMessage());
            }

            UILabel lbCraftCount = (UILabel)view.findById("lb_count");
            lbCraftCount.setString(job.getTotalCount() == Integer.MAX_VALUE ? "xx" : "x" + job.getCount());
            lbCraftCount.setOnClickListener(v -> {
                job.setTotalCount(job.getTotalCount() == Integer.MAX_VALUE ? 1 : Integer.MAX_VALUE);
            });
            lbCraftCount.setOnRightClickListener(v -> {
                job.setTotalCount(job.getTotalCount() == Integer.MAX_VALUE ? 1 : Integer.MAX_VALUE);
            });

            view.setPosition(0, 40 + index * 80);
            _frameCraftEntries.addView(view);
        });
    }

    private void addActionMenuEntry(ItemInfo.ItemInfoAction action, int index) {
        UILabel lbCraft = _viewFactory.createTextView();
        lbCraft.setString(action.label);
        lbCraft.setCharacterSize(14);
        lbCraft.setPosition(0, 0 + 28 * index);
        lbCraft.setPadding(0, 8);
        lbCraft.setSize(220, 28);
        lbCraft.setBackgroundColor(new Color(0x1d5560));
        lbCraft.setAlign(Align.CENTER_VERTICAL);
        lbCraft.setOnClickListener(view -> {
            JobHelper.addJob(_item, action);
        });
        _menuAddCraftEntries.addView(lbCraft);
    }

    @Override
    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
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
