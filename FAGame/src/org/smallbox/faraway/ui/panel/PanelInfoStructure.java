package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.StructureModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoStructure extends BaseInfoRightPanel {
    private StructureModel _structure;

    public PanelInfoStructure(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_structure.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        super.onLayoutLoaded(layout);

        if (_structure != null) {
            select(_structure);
        }
    }

    public void select(StructureModel structure) {
        super.select(structure.getArea());

        _structure = structure;

        if (isLoaded()) {
            ((TextView)findById("lb_name")).setString(structure.getName());
            ((TextView)findById("lb_label")).setString(structure.getLabel());
            ((TextView)findById("lb_durability")).setString("Durability: " + structure.getHealth());
            ((TextView)findById("lb_matter")).setString("Matter: " + structure.getMatter());
            ((TextView)findById("lb_pos")).setString("Pos: " + structure.getX() + "x" + structure.getY());
            ((TextView)findById("lb_health")).setString("Health: " + structure.getHealth() + "/" + structure.getMaxHealth());
            ((TextView)findById("lb_work")).setString("Work remaining: " + structure.getProgress() + "/" + structure.getInfo().cost);
        }
    }
}
