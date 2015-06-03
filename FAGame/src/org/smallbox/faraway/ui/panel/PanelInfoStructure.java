package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.model.item.WorldArea;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoStructure extends BaseRightPanel {
    private StructureItem _structure;

    public PanelInfoStructure(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_structure.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        if (_structure != null) {
            select(_structure);
        }
    }

    public void select(StructureItem structure) {
        _structure = structure;

        if (isLoaded()) {
            ((TextView)findById("lb_name")).setString(structure.getLabel());
            ((TextView)findById("lb_durability")).setString("Durability: " + structure.getHealth());
            ((TextView)findById("lb_matter")).setString("Matter: " + structure.getMatter());
            ((TextView)findById("lb_pos")).setString("Pos: " + structure.getX() + "x" + structure.getY());
        }
    }
}
