package org.smallbox.faraway.modules.panels.info;

import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoConsumableModule extends GameUIModule {
    private class PanelInfoResourceModuleWindow extends UIWindow {
        private ConsumableModel _consumable;
        private ItemInfo        _itemInfo;

        @Override
        protected void onCreate(UIWindow window, FrameLayout content) {
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

        @Override
        protected String getContentLayout() {
            return "panels/info/consumable";
        }

        public void select(ConsumableModel consumable) {
            _consumable = consumable;
            _itemInfo = consumable.getInfo();
            select(consumable.getInfo());

            if (isLoaded()) {
                ((UILabel)findById("lb_durability")).setText("Durability: " + _consumable.getHealth());
                ((UILabel)findById("lb_matter")).setText("Matter: " + _consumable.getMatter());
                ((UILabel)findById("lb_quantity")).setText("Quantity: %d", _consumable.getQuantity());

                if (consumable.getJobs() != null && !consumable.getJobs().isEmpty()) {
                    String str = "Job:\n";
                    for (BaseJobModel job: consumable.getJobs()) {
                        str += job.getLabel() + " (" + job.getMessage() + ")\n";
                    }
                    ((UILabel)findById("lb_job")).setText(str);
                } else {
                    ((UILabel)findById("lb_job")).setText("");
                }
            }
        }

        public void select(ItemInfo info) {
            _itemInfo = info;

            if (isLoaded()) {
                ((UILabel)findById("lb_name")).setText(_itemInfo.name);
                ((UILabel)findById("lb_label")).setText(_itemInfo.label);
            }
        }
    }

    private PanelInfoResourceModuleWindow _window;

    @Override
    public void onLoaded() {
        _window = new PanelInfoResourceModuleWindow();
        addWindow(_window);
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onSelectConsumable(ConsumableModel consumable) {
        _window.select(consumable);
        _window.setVisible(true);
    }

    @Override
    public void onDeselect() {
        _window.setVisible(false);
    }
}
