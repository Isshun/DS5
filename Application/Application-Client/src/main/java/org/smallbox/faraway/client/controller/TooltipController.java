//package org.smallbox.faraway.client.controller;
//
//import com.badlogic.gdx.Input;
//import org.smallbox.faraway.client.controller.annotation.BindLua;
//import org.smallbox.faraway.client.render.Viewport;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.client.ui.engine.views.widgets.View;
//import org.smallbox.faraway.core.GameShortcut;
//import org.smallbox.faraway.common.dependencyInjector.BindComponent;
//import org.smallbox.faraway.common.dependencyInjector.GameObject;
//import org.smallbox.faraway.modules.character.CharacterModule;
//import org.smallbox.faraway.modules.world.WorldModule;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Created by Alex on 26/04/2016.
// */
//@GameObject
//public class TooltipController extends LuaController {
//
//    @BindLua
//    private UILabel lbName;
//
//    @BindComponent
//    private WorldModule worldModule;
//
//    @BindComponent
//    private CharacterModule characterModule;
//
//    @BindComponent
//    private Viewport viewport;
//
//    private Map<String, View> subViews = new ConcurrentHashMap<>();
//
//    public void addSubView(String name, View view) {
//        subViews.put(name, view);
//
//        refreshLabels();
//    }
//
//    public void removeSubView(String name) {
//        subViews.remove(name);
//
//        for (View subView: subViews.values()) {
//            subView.setVisible(true);
//            break;
//        }
//
//        refreshLabels();
//    }
//
//    private void refreshLabels() {
//        StringBuilder sb = new StringBuilder();
//        subViews.forEach((key, value) -> {
//            if (value.isVisible()) {
//                sb.append("[").append(key.toUpperCase()).append("] ");
//            } else {
//                sb.append(" ").append(key).append("  ");
//            }
//        });
//        lbName.setText(sb.toString());
//    }
//
//    @GameShortcut(key = Input.Keys.TAB)
//    public void onNextView() {
//        boolean selectNext = false;
//        for (View view: subViews.values()) {
//            if (selectNext) {
//                view.setVisible(true);
//                refreshLabels();
//                return;
//            }
//            if (view.isVisible()) {
//                selectNext = true;
//            }
//        }
//        for (View view: subViews.values()) {
//            view.setVisible(true);
//            refreshLabels();
//            return;
//        }
//    }
//
//}
