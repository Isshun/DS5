package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.world.WorldModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugGroundLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;

    public void    onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getAll().stream()
                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> {
                    if (parcel.getGroundInfo() != null) {
                        renderer.drawRectangleOnMap(parcel, TILE_SIZE, TILE_SIZE, Color.CORAL, 0, 0);

                        renderer.drawTextOnMap(parcel, parcel.getGroundInfo().label, Color.BLACK, 14, 1, 1);
                        renderer.drawTextOnMap(parcel, parcel.getGroundInfo().label, Color.WHITE, 14, 0, 0);

                        if (parcel.getRockInfo() != null) {
                            renderer.drawTextOnMap(parcel, parcel.getRockInfo().label, Color.BLACK, 14, 1, 10);
                            renderer.drawTextOnMap(parcel, parcel.getRockInfo().label, Color.WHITE, 14, 0, 9);
                        }

                        renderer.drawTextOnMap(parcel, String.valueOf(parcel.z), Color.BLACK, 8, 1, 20);
                        renderer.drawTextOnMap(parcel, String.valueOf(parcel.z), Color.WHITE, 8, 0, 19);
                    }

                });
    }

    @GameShortcut(key = Input.Keys.F9)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}
