package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.TextView;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoResource extends BaseInfoRightPanel {
    private ResourceModel   _resource;

    public PanelInfoResource(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_resource.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        super.onLayoutLoaded(layout);

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
            ((TextView) findById("lb_label")).setString(resource.getLabel());
            ((TextView) findById("lb_name")).setString(resource.getName());
            ((TextView) findById("lb_quantity")).setString("Quantity: %d", resource.getQuantity());

            if (findById("lb_pos") != null) {
                ((TextView) findById("lb_pos")).setString(resource.getX() + "x" + resource.getY());
            }
        }
    }
}
