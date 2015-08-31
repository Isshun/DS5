package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoResource extends BaseInfoRightPanel {
    private ResourceModel   _resource;

    public PanelInfoResource(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_resource.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        super.onLayoutLoaded(layout, panel);

        if (_resource != null) {
            select(_resource);
        }
    }

    @Override
    protected void onRefresh(int update) {
        if (_resource != null && _resource.needRefresh()) {
            select(_resource);
        }
    }

    public void select(ResourceModel resource) {
        super.select(resource.getParcel());

        _resource = resource;

        if (isLoaded()) {
            ((UILabel) findById("lb_label")).setString(resource.getLabel());
            ((UILabel) findById("lb_name")).setString(resource.getName());
            ((UILabel) findById("lb_quantity")).setString("Quantity: %d", resource.getQuantity());

            UILabel lbGrowState = (UILabel) findById("lb_grow_state");
            UILabel lbGrowSpeed = (UILabel) findById("lb_grow_speed");
            if (resource.isPlant()) {
                lbGrowState.setString("Grow: " + (int) (resource.getRealQuantity() * 100 / resource.getTotalQuantity()) + "%");
                lbGrowState.setVisible(true);

                if (resource.getGrowState().name != null) {
                    lbGrowSpeed.setString("Grow speed: " + (int) (resource.getGrowRate() * 100) + "% (" + resource.getGrowState().name + ")");
                } else {
                    lbGrowSpeed.setString("Grow speed: " + (int) (resource.getGrowRate() * 100) + "%");
                }
                lbGrowSpeed.setVisible(true);
            } else {
                lbGrowState.setVisible(false);
                lbGrowSpeed.setVisible(false);
            }

            if (findById("lb_pos") != null) {
                ((UILabel) findById("lb_pos")).setString(resource.getX() + "x" + resource.getY());
            }
        }
    }
}
