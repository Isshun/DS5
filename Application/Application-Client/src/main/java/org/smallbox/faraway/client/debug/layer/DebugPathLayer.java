//package org.smallbox.faraway.client.debug.layer;
//
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.Color;
//import org.smallbox.faraway.client.render.LayerManager;
//import org.smallbox.faraway.client.render.Viewport;
//import org.smallbox.faraway.client.render.layer.BaseLayer;
//import org.smallbox.faraway.client.render.layer.GDXRenderer;
//import org.smallbox.faraway.core.GameLayer;
//import org.smallbox.faraway.core.GameShortcut;
//import org.smallbox.faraway.common.dependencyInjector.BindComponent;
//import org.smallbox.faraway.common.dependencyInjector.GameObject;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.modules.world.WorldModule;
//
///**
// * Created by Alex on 31/07/2016.
// */
//@GameObject
//@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
//public class DebugPathLayer extends BaseLayer {
//
//    @BindComponent
//    private WorldModule worldModule;
//
//    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
//        worldModule.getParcelList().stream()
//                .filter(viewport::hasParcel)
//                .forEach(parcel -> {
//                    // Border
//                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 31, 31, parcel.isWalkable() ? Color.GREEN : Color.RED, false, 0, 0);
//
//                    Color color;
//
//                    // Top
//                    color = parcel.hasConnection(WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z)) ? Color.GREEN : Color.RED;
//                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 1, 6, color, true, 16, 0);
//
//                    // Bottom
//                    color = parcel.hasConnection(WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z)) ? Color.GREEN : Color.RED;
//                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 1, 6, color, true, 16, 26);
//
//                    // Left
//                    color = parcel.hasConnection(WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z)) ? Color.GREEN : Color.RED;
//                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 6, 1, color, true, 0, 16);
//
//                    // Right
//                    color = parcel.hasConnection(WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z)) ? Color.GREEN : Color.RED;
//                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 6, 1, color, true, 26, 16);
//                });
//    }
//
//    @GameShortcut(key = Input.Keys.F7)
//    public void onToggleVisibility() {
//        toggleVisibility();
//    }
//
//}
