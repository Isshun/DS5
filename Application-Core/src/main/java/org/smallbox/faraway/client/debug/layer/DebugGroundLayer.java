package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.WorldModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.INFO_LEVEL, visible = false)
public class DebugGroundLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
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

}
