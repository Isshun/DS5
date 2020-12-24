//package org.smallbox.faraway.module.dev;
//
//import org.smallbox.faraway.game.module.*;
//import org.smallbox.faraway.module.world.TemperatureModule;
//import org.smallbox.faraway.ui.engine.views.UIFrame;
//import org.smallbox.faraway.util.StringUtils;
//
///**
// * Created by Alex
// */
//public class TemperatureDebugModule extends GameUIModule {
//    @Override
//    protected boolean loadOnStart() {
//        return false;
//    }
//
//    @Override
//    protected void onGameStart() {
//        DebugWindow window = (DebugWindow)WindowDebugBuilder.createModules().setTitle("Temperature Debug").build(new UIWindowListener() {
//            @Override
//            public void onGameInit(UIWindow window, UIFrame view) {
//                window.setPosition(500, 500);
//            }
//
//            @Override
//            public void onRefresh(int updateGame) {
//            }
//
//            @Override
//            public void onClose() {
//            }
//        });
//
//        TemperatureModule module = (TemperatureModule)Application.moduleManager.getModule(TemperatureModule.class);
//
//        window.addDebugView("increase", view -> module.increaseTemperature());
//        window.addDebugView("decrease", view -> module.decreaseTemperature());
//        window.addDebugView("normalize", view -> module.normalize());
//        window.addDebugView("temperature: " + StringUtils.formatNumber(module.getTemperature()));
//        window.addDebugView("temperature target: " + StringUtils.formatNumber(module.getTemperatureTarget()));
//        window.addDebugView("temperature offset: " + StringUtils.formatNumber(module.getTemperatureOffset()));
//
//        addWindow(window);
//    }
//
//    @Override
//    protected void onGameUpdate(int tick) {
//    }
//}
