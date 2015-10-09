//package org.smallbox.faraway.ui.panel;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.ui.LayoutModel;
//import org.smallbox.faraway.ui.UserInterface;
//import org.smallbox.faraway.ui.engine.Colors;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.view.UIFrame;
//import org.smallbox.faraway.ui.engine.view.UIImage;
//import org.smallbox.faraway.ui.engine.view.UILabel;
//import org.smallbox.faraway.ui.engine.view.View;
//
///**
// * Created by Alex on 13/06/2015.
// */
//public class PanelTopRight extends BasePanel {
//
//    public PanelTopRight() {
//        super(UserInterface.Mode.NONE, null, "data/ui/panels/top_right.yml");
//    }
//
//    @Override
//    protected void onCreate(ViewFactory factory) {
//        setBackgroundColor(Colors.BT_INACTIVE);
//
//        View border = factory.createColorView(_width, 4);
//        border.setBackgroundColor(Colors.BACKGROUND);
//        border.setPosition(_x, _y + _height);
//        addView(border);
//
//        setAlwaysVisible(true);
//    }
//
//    @Override
//    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
//    }
//
//    @Override
//    public void onRefresh(int tick) {
//        ((UILabel)findById("lb_speed")).setText("Speed: " + Game.getInstance().getSpeed());
//
//        if (Game.getInstance().isRunning()) {
//            switch (Game.getInstance().getSpeed()) {
//                case 3:
//                    ((UIImage) findById("img_speed")).setImage("data/res/ic_speed_3.png");
//                    break;
//                case 2:
//                    ((UIImage) findById("img_speed")).setImage("data/res/ic_speed_2.png");
//                    break;
//                default:
//                    ((UIImage) findById("img_speed")).setImage("data/res/ic_speed_1.png");
//                    break;
//            }
//        } else {
//            ((UIImage) findById("img_speed")).setImage("data/res/ic_speed_0.png");
//        }
//    }
//}
