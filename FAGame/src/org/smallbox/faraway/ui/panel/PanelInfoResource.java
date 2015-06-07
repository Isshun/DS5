package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.WorldResource;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoResource extends BaseRightPanel {
    private TextView        _lbName;
    private WorldResource   _resource;

    public PanelInfoResource(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_resource.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        select(_resource);
    }

    @Override
    protected void onRefresh(int update) {
        if (_resource != null && _resource.needRefresh()) {
            select(_resource);
        }
    }

    public void select(WorldResource resource) {
        _resource = resource;

        if (resource != null) {
            ((TextView) findById("lb_label")).setString(resource.getLabel());
            ((TextView) findById("lb_name")).setString(resource.getName());
            ((TextView) findById("lb_quantity")).setString("Quantity: %d", resource.getQuantity());
        }
    }
}
