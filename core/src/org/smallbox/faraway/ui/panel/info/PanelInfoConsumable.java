//package org.smallbox.faraway.ui.panel.info;
//
//import org.smallbox.faraway.engine.GameEventListener;
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.model.item.ConsumableModel;
//import org.smallbox.faraway.game.model.item.ItemInfo;
//import org.smallbox.faraway.game.model.job.BaseJobModel;
//import org.smallbox.faraway.ui.LayoutModel;
//import org.smallbox.faraway.ui.UserInterface;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.views.UIFrame;
//import org.smallbox.faraway.ui.engine.views.UILabel;
//import org.smallbox.faraway.ui.panel.BaseRightPanel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class PanelInfoConsumable extends BaseRightPanel {
//    private ConsumableModel _consumable;
//    private ItemInfo    _itemInfo;
//
//    public PanelInfoConsumable(UserInterface.Mode mode, GameEventListener.Key shortcut) {
//        super(mode, shortcut, "data/ui/panels/info_consumable.yml");
//    }
//
//    @Override
//    public void onCreate(ViewFactory factory) {
//    }
//
//    @Override
//    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
//        if (_consumable != null) {
//            select(_consumable);
//        } else if (_itemInfo != null) {
//            select(_itemInfo);
//        }
//
//        addDebugView("Remove item", 24, 400, view -> ModuleHelper.getWorldModule().removeConsumable(_consumable));
//    }
//
//    @Override
//    protected void onRefresh(int update) {
//        if (_consumable != null && _consumable.needRefresh()) {
//            select(_consumable);
//        }
//    }
//
//    public void select(ConsumableModel consumable) {
//        _consumable = consumable;
//        _itemInfo = consumable.getInfo();
//        select(consumable.getInfo());
//
//        if (isLoaded()) {
//            ((UILabel)findById("lb_durability")).setText("Durability: " + _consumable.getHealth());
//            ((UILabel)findById("lb_matter")).setText("Matter: " + _consumable.getMatter());
//            ((UILabel)findById("lb_quantity")).setText("Quantity: %d", _consumable.getQuantity());
//
//            if (consumable.getJobs() != null && !consumable.getJobs().isEmpty()) {
//                String str = "Job:\n";
//                for (BaseJobModel job: consumable.getJobs()) {
//                    str += job.getLabel() + " (" + job.getMessage() + ")\n";
//                }
//                ((UILabel)findById("lb_job")).setText(str);
//            } else {
//                ((UILabel)findById("lb_job")).setText("");
//            }
//        }
//    }
//
//    public void select(ItemInfo info) {
//        _itemInfo = info;
//
//        if (isLoaded()) {
//            ((UILabel)findById("lb_name")).setText(_itemInfo.name);
//            ((UILabel)findById("lb_label")).setText(_itemInfo.label);
//        }
//    }
//
//
//}
