package org.smallbox.faraway.game.module.panels.info;

import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoResourceModule extends GameUIModule {
    private class PanelInfoResourceModuleWindow extends UIWindow {
        private ResourceModel _resource;
        private UILabel         _lbLabel;
        private UILabel         _lbName;
        private UILabel         _lbQuantity;
        private UILabel         _lbGrowState;
        private UILabel         _lbGrowSpeed;
        private UILabel         _lbPos;

        @Override
        protected void onCreate(UIWindow window, FrameLayout content) {
            _lbLabel = (UILabel) content.findById("lb_label");
            _lbName = (UILabel) content.findById("lb_name");
            _lbQuantity = (UILabel) content.findById("lb_quantity");
            _lbGrowState = (UILabel) content.findById("lb_grow_state");
            _lbGrowSpeed = (UILabel) content.findById("lb_grow_speed");
            _lbPos = (UILabel) content.findById("lb_pos");
        }

        @Override
        protected void onRefresh(int update) {
            if (_resource != null) {
                _lbLabel.setText(_resource.getLabel());
                _lbName.setText(_resource.getName());
                _lbQuantity.setText("Quantity: %d", _resource.getQuantity());

                if (_resource.isPlant()) {
                    _lbGrowState.setText("Grow: " + (int) (_resource.getRealQuantity() * 100 / _resource.getTotalQuantity()) + "%");
                    _lbGrowState.setVisible(true);

                    if (_resource.getGrowState() != null) {
                        _lbGrowSpeed.setText("Grow speed: " + (int) (_resource.getGrowRate() * 100) + "% (" + _resource.getGrowState().name + ")");
                    } else {
                        _lbGrowSpeed.setText("Grow speed: " + (int) (_resource.getGrowRate() * 100) + "%");
                    }
                    _lbGrowSpeed.setVisible(true);
                } else {
                    _lbGrowState.setVisible(false);
                    _lbGrowSpeed.setVisible(false);
                }

                if (_lbPos != null) {
                    _lbPos.setText(_resource.getX() + "x" + _resource.getY());
                }
            }        }

        @Override
        protected String getContentLayout() {
            return "panels/info_resource.yml";
        }

        public void select(ResourceModel resource) {
            this._resource = resource;
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
    public void onSelectResource(ResourceModel resource) {
        _window.select(resource);
        _window.setVisible(true);
    }

    @Override
    public void onDeselect() {
        _window.select(null);
        _window.setVisible(false);
    }
}
