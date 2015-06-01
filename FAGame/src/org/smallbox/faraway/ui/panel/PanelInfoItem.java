package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.GameTimer;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoItem extends BaseRightPanel {
    private UserItem    _item;
    private ItemInfo    _itemInfo;
    private ViewFactory _viewFactory;
    private FrameLayout _frameCraft;
    private FrameLayout _frameCraftEntries;
    private FrameLayout _menuAddCraft;
    private FrameLayout _menuAddCraftEntries;
    private View        _btAddCraft;

    public PanelInfoItem(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_item.yml");
    }
    
    @Override
    public void onCreate(ViewFactory factory) {
        _viewFactory = factory;
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
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

    public void select(UserItem item) {
        _item = item;
        _itemInfo = item.getInfo();
        select(item.getInfo());

        if (isLoaded()) {
            ((TextView)findById("lb_durability")).setString("Durability: " + _item.getHealth());
            ((TextView)findById("lb_matter")).setString("Matter: " + _item.getMatter());
            ((TextView)findById("lb_pos")).setString("Pos: " + _item.getX() + "x" + _item.getY());
        }

        if (_itemInfo.actions != null && !_itemInfo.actions.isEmpty()) {
            _frameCraft.setVisible(true);
            _frameCraftEntries.clearAllViews();
            _menuAddCraftEntries.clearAllViews();

            // Create menu
            int index = 0;
            for (ItemInfo.ItemInfoAction action: _itemInfo.actions) {
                addActionMenuEntry(action, index++);
            }

            // Create actions list
            index = 0;
            if (_item.hasJobs()) {
                for (Job job : _item.getJobs()) {
                    addJobListEntry(job, index++);
                }
            }
        }
    }

    public void select(ItemInfo info) {
        _itemInfo = info;

        if (isLoaded()) {
            ((TextView)findById("lb_name")).setString(_itemInfo.label);
        }
    }

    private void addJobListEntry(Job job, int index) {
        TextView lbEnable = _viewFactory.createTextView();
        lbEnable.setString("[x]");
        lbEnable.setCharacterSize(14);
        lbEnable.setPosition(24, 20 + 20 * index);
        _frameCraftEntries.addView(lbEnable);

        TextView lbCraft = _viewFactory.createTextView();
        lbCraft.setString(job.getLabel());
        lbCraft.setCharacterSize(14);
        lbCraft.setPosition(80, 20 + 20 * index);
        _frameCraftEntries.addView(lbCraft);

        TextView lbCraftCount = _viewFactory.createTextView();
        lbCraftCount.setString("x1");
        lbCraftCount.setCharacterSize(14);
        lbCraftCount.setPosition(250, 20 + 20 * index);
        _frameCraftEntries.addView(lbCraftCount);

        TextView lbCraftCancel = _viewFactory.createTextView();
        lbCraftCancel.setString("cancel");
        lbCraftCancel.setCharacterSize(14);
        lbCraftCancel.setPosition(300, 20 + 20 * index);
        lbCraftCancel.setOnClickListener(view -> {
            JobManager.getInstance().removeJob(job);
        });
        lbCraftCancel.resetSize();
        _frameCraftEntries.addView(lbCraftCancel);
    }

    private void addActionMenuEntry(ItemInfo.ItemInfoAction action, int index) {
        TextView lbCraft = _viewFactory.createTextView();
        lbCraft.setString(action.label);
        lbCraft.setCharacterSize(14);
        lbCraft.setPosition(0, 0 + 20 * index);
        lbCraft.setSize(lbCraft.getContentWidth(), lbCraft.getContentHeight());
        lbCraft.setOnClickListener(view -> JobManager.getInstance().addCraftJob(_item, action));
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
