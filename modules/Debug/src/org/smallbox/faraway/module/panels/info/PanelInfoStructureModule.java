//package org.smallbox.faraway.module.panels.info;
//
//import org.smallbox.faraway.game.helper.JobHelper;
//import org.smallbox.faraway.game.model.ReceiptModel;
//import org.smallbox.faraway.game.model.item.StructureModel;
//import org.smallbox.faraway.game.module.GameUIModule;
//import org.smallbox.faraway.game.module.UIWindow;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.views.UIFrame;
//import org.smallbox.faraway.ui.engine.views.UILabel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class PanelInfoStructureModule extends GameUIModule {
//    private class PanelInfoStructureModuleWindow extends UIWindow {
//        private StructureModel _structure;
//
//        @Override
//        protected void onCreate(UIWindow window, UIFrame content) {
//            if (_structure != null) {
//                select(_structure);
//            }
//        }
//
//        @Override
//        protected void onRefresh(int update) {
//            if (_structure != null && _structure.needRefresh()) {
//                select(_structure);
//            }
//        }
//
//        @Override
//        protected String getContentLayout() {
//            return "panels/info/structure";
//        }
//
//        public void select(StructureModel structure) {
//            _structure = structure;
//
//            if (isLoaded()) {
//                findById("frame_info").setVisible(true);
//
//                ((UILabel) findById("lb_name")).setText(structure.getName());
//                ((UILabel) findById("lb_label")).setText(structure.getLabel());
//                ((UILabel) findById("lb_durability")).setText("Durability: " + structure.getHealth());
//                ((UILabel) findById("lb_matter")).setText("Matter: " + structure.getMatter());
//                ((UILabel) findById("lb_pos")).setText("Pos: " + structure.getX() + "x" + structure.getY());
//                ((UILabel) findById("lb_health")).setText("Health: " + structure.getHealth() + "/" + structure.getMaxHealth());
//                ((UILabel) findById("lb_work")).setText("Work remaining: " + structure.getProgress() + "/" + structure.getInfo().cost);
//
//                findById("bt_destroy").setOnClickListener(view -> JobHelper.addDumpJob(structure));
//
//                if (findById("lb_pos") != null) {
//                    ((UILabel) findById("lb_pos")).setText(structure.getX() + "x" + structure.getY());
//                }
//
//                if (structure.getJobBuild() != null && structure.getJobBuild().getReceipt() != null) {
//                    ((UIFrame)findById("frame_components_entries")).removeAllViews();
//                    int orderIndex = 0;
//                    for (ReceiptModel.OrderModel order : structure.getJobBuild().getReceipt().getOrders()) {
//                        addJobOrder((UIFrame)findById("frame_components_entries"), order, orderIndex++);
//                    }
//                    findById("frame_components_entries").setVisible(true);
//                } else {
//                    findById("frame_components_entries").setVisible(false);
//                }
//
//            }
//        }
//
//        protected void addJobOrder(UIFrame frame, ReceiptModel.OrderModel order, int index) {
//            UILabel lbOrder = ViewFactory.getInstance().createTextView();
//            lbOrder.setTextSize(14);
//            lbOrder.setPosition(0, index * 20);
//
//            String str = order.consumable.getInfo().label;
//            switch (order.status) {
//                case NONE: lbOrder.setDashedString(str, "waiting", 42); break;
//                case CARRY: lbOrder.setDashedString(str, "carrying", 42); break;
//                case STORED: lbOrder.setDashedString(str, "ok", 42); break;
//            }
//
//
//            frame.addView(lbOrder);
//        }
//    }
//
//    private PanelInfoStructureModuleWindow _window;
//
//    @Override
//    public void onLoaded() {
//        _window = new PanelInfoStructureModuleWindow();
//        addWindow(_window);
//    }
//
//    @Override
//    protected void onUpdate(int tick) {
//    }
//
//    @Override
//    public void onSelectStructure(StructureModel structure) {
//        _window.select(structure);
//        _window.setVisible(true);
//    }
//
//    @Override
//    public void onDeselect() {
//        _window.setVisible(false);
//    }
//}
