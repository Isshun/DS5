//package org.smallbox.faraway.renders;
//
//import org.smallbox.faraway.client.renderer.Viewport;
//import org.smallbox.faraway.core.engine.Color;
//import org.smallbox.faraway.client.renderer.BaseRenderer;
//import org.smallbox.faraway.client.renderer.GDXRenderer;
//import org.smallbox.faraway.core.game.model.GameConfig;
//import org.smallbox.faraway.core.world.model.ParcelModel;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//import org.smallbox.faraway.util.Constant;
//import UIManager;
//import UILabel;
//import View;
//
//public class DebugRenderer extends BaseRenderer {
//
//    public DebugRenderer() {
////        _cache = ViewFactory.getInstance().createRenderLayer(0, 0, 0);
//    }
//
//    @Override
//    public void onRefresh(int frame) {
////        UILabel lbDebug = new UILabel();
////        lbDebug.setTextSize(12);
////
////        _cache.begin();
////
////        for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
////
////            lbDebug.setText(String.valueOf(parcel.getLight()));
////            lbDebug.setTextSize(14);
////            lbDebug.setTextColor(Color.WHITE);
//////            lbDebug.setPosition((int) (parcel.x * Constant.TILE_WIDTH), (int) (parcel.y * Constant.TILE_HEIGHT));
////            lbDebug.setPosition(0, 0);
////            _cache.draw(lbDebug);
////        }
////        _cache.end();
//    }
//
//    @Override
//    public boolean isActive(GameConfig configurationManager) {
//        return configurationManager.render.debug;
//    }
//
//    @Override
//    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress) {
//        UILabel lbDebug = new UILabel();
//        lbDebug.setTextSize(14);
//        lbDebug.setTextColor(Color.WHITE);
//        lbDebug.setPosition(0, 0);
//        lbDebug.setSize(32, 32);
//        lbDebug.setTextAlign(View.Align.CENTER);
//
////        int relX = ApplicationClient.uiManager.getMouseX();
////        int relY = ApplicationClient.uiManager.getMouseY();
//
//        for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
//            if (parcel.z == 0) {
////                Color color = new Color(parcel.getResource().getQuantity() * 250 / 1300, 0, 0);
////                renderer.draw(color, (int) (parcel.x * Constant.TILE_WIDTH + viewport.getPosX()), (int) (parcel.y * Constant.TILE_HEIGHT + viewport.getPosY()), 32, 32);
//            }
//        }
//
////        for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
////            if (parcel.getZ() == 0 && parcel.x > relX - 8 && parcel.x < relX + 8 && parcel.y > relY - 8 && parcel.y < relY + 8) {
////                lbDebug.setText(String.valueOf((int)(parcel.getLight() * 10)));
//////                lbDebug.setText(parcel.x + "x" + parcel.y);
////                renderer.draw(lbDebug, (int) (parcel.x * Constant.TILE_WIDTH + effect.getViewport().getPosX()), (int) (parcel.y * Constant.TILE_HEIGHT + effect.getViewport().getPosY()));
////            }
////        }
//    }
//
//}