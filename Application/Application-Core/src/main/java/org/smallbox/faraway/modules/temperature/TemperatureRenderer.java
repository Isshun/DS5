//package org.smallbox.faraway.modules.temperature;
//
//import com.badlogic.gdx.graphics.Color;
//import org.smallbox.faraway.client.layer.GDXLayer;
//import org.smallbox.faraway.client.layer.GameDisplay;
//import org.smallbox.faraway.client.layer.Viewport;
//import org.smallbox.faraway.core.dependencyInjector.BindModule;
//import org.smallbox.faraway.core.module.room.model.RoomModel;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.module.room.RoomModule;
//import org.smallbox.faraway.util.Constant;
//
///**
// * Created by Alex on 14/06/2015.
// */
//public class TemperatureLayer extends GameDisplay {
//
//    @BindComponent
//    private RoomModule roomModule;
//
//    @Override
//    public void onDraw(GDXLayer layer, Viewport viewport, double animProgress) {
//        for (RoomModel room: roomModule.getRooms()) {
//            if (!room.isExterior()) {
//                int minX = Integer.MAX_VALUE;
//                int minY = Integer.MAX_VALUE;
//                int maxX = Integer.MIN_VALUE;
//                int maxY = Integer.MIN_VALUE;
//                float offset = (float) (Math.max(-50, Math.min(50, 21 - room.getTemperatureInfo().temperature)) / 50f);
//                Color color = offset > 0 ? new Color(1f - offset, 1f - offset, 1f, 0.8f) : new Color(1f, 1f - offset, 1f - offset, 0.8f);
//                for (ParcelModel parcel : room.getParcelsByType()) {
//                    renderer.draw(color,
//                            parcel.x * Constant.TILE_WIDTH + viewport.getPosX(),
//                            parcel.y * Constant.TILE_HEIGHT + viewport.getPosY(),
//                            32,
//                            32);
//                    if (parcel.x > maxX) maxX = parcel.x;
//                    if (parcel.y > maxY) maxY = parcel.y;
//                    if (parcel.x < minX) minX = parcel.x;
//                    if (parcel.y < minY) minY = parcel.y;
//                }
//
//                String text = (int)Math.round(room.getTemperatureInfo().temperature) + "°";
////                BitmapFont.TextBounds bounds = layer.getFont(24).getWrappedBounds(text, 0);
//                renderer.draw(text, 24,
//                        (minX + (maxX - minX) / 2) * Constant.TILE_WIDTH + viewport.getPosX(),
//                        (minY + (maxY - minY) / 2) * Constant.TILE_HEIGHT + viewport.getPosY(),
//                        Color.BLACK);
//            }
//        }
//    }
//
//    @Override
//    public void onRefresh(int frame) {
//    }
//
//    @Override
//    public String getName() {
//        return "temperature";
//    }
//}
