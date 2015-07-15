package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.JobHelper;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.job.BaseBuildJobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.UILabel;

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

    @Override
    protected void onRefresh(int update) {
        if (_structure != null && _structure.needRefresh()) {
            select(_structure);
        }
    }

    public void select(StructureModel structure) {
        super.select(structure.getParcel());

        _structure = structure;

        if (isLoaded()) {
            findById("frame_info").setVisible(true);

            ((UILabel) findById("lb_name")).setString(structure.getName());
            ((UILabel) findById("lb_label")).setString(structure.getLabel());
            ((UILabel) findById("lb_durability")).setString("Durability: " + structure.getHealth());
            ((UILabel) findById("lb_matter")).setString("Matter: " + structure.getMatter());
            ((UILabel) findById("lb_pos")).setString("Pos: " + structure.getX() + "x" + structure.getY());
            ((UILabel) findById("lb_health")).setString("Health: " + structure.getHealth() + "/" + structure.getMaxHealth());
            ((UILabel) findById("lb_work")).setString("Work remaining: " + structure.getProgress() + "/" + structure.getInfo().cost);

            findById("bt_destroy").setOnClickListener(view -> JobHelper.addDumpJob(structure));

            if (findById("lb_pos") != null) {
                ((UILabel) findById("lb_pos")).setString(structure.getX() + "x" + structure.getY());
            }

            if (structure.getJobBuild() != null && structure.getJobBuild().getReceipt() != null) {
                ((FrameLayout)findById("frame_components_entries")).removeAllViews();
                int orderIndex = 0;
                for (ReceiptModel.OrderModel order : structure.getJobBuild().getReceipt().getOrders()) {
                    addJobOrder((FrameLayout)findById("frame_components_entries"), order, orderIndex++);
                }
                findById("frame_components_entries").setVisible(true);
            } else {
                findById("frame_components_entries").setVisible(false);
            }

        }
    }
}
