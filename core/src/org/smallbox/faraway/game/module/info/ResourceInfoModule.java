package org.smallbox.faraway.game.module.info;

import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;

/**
 * Created by Alex on 01/06/2015.
 */
public class ResourceInfoModule extends GameUIModule {
    private class ResourceInfoModuleWindow extends UIWindow {
        private boolean         _isLoaded;
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
            _isLoaded = true;
        }

        @Override
        protected void onRefresh(int update) {

        }

        @Override
        protected String getContentLayout() {
            return "panels/info_resource.yml";
        }

        public void select(ResourceModel resource) {
            if (_isLoaded && resource != null) {
                _lbLabel.setString(resource.getLabel());
                _lbName.setString(resource.getName());
                _lbQuantity.setString("Quantity: %d", resource.getQuantity());

                if (resource.isPlant()) {
                    _lbGrowState.setString("Grow: " + (int) (resource.getRealQuantity() * 100 / resource.getTotalQuantity()) + "%");
                    _lbGrowState.setVisible(true);

                    if (resource.getGrowState().name != null) {
                        _lbGrowSpeed.setString("Grow speed: " + (int) (resource.getGrowRate() * 100) + "% (" + resource.getGrowState().name + ")");
                    } else {
                        _lbGrowSpeed.setString("Grow speed: " + (int) (resource.getGrowRate() * 100) + "%");
                    }
                    _lbGrowSpeed.setVisible(true);
                } else {
                    _lbGrowState.setVisible(false);
                    _lbGrowSpeed.setVisible(false);
                }

                if (_lbPos != null) {
                    _lbPos.setString(resource.getX() + "x" + resource.getY());
                }
            }
        }
    }

    private ResourceInfoModuleWindow    _window;

    @Override
    public void onLoaded() {
        _window = new ResourceInfoModuleWindow();
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
