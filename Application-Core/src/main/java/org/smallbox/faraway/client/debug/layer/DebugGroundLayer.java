package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.world.WorldModule;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugGroundLayer extends BaseLayer {
    @Inject private WorldModule worldModule;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getAll().stream()
                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> {
                    if (parcel.getGroundInfo() != null) {
                        renderer.drawPixelOnMap(parcel.x, parcel.y, Color.CORAL);

                        renderer.drawTextOnMap(parcel, parcel.getGroundInfo().label, 14, Color.BLACK, 1, 1);
                        renderer.drawTextOnMap(parcel, parcel.getGroundInfo().label, 14, Color.WHITE);

                        if (parcel.getRockInfo() != null) {
                            renderer.drawTextOnMap(parcel, parcel.getRockInfo().label, 14, Color.BLACK, 1, 10);
                            renderer.drawTextOnMap(parcel, parcel.getRockInfo().label, 14, Color.WHITE, 0, 9);
                        }

                        renderer.drawTextOnMap(parcel, String.valueOf(parcel.z), 8, Color.BLACK, 1, 20);
                        renderer.drawTextOnMap(parcel, String.valueOf(parcel.z), 8, Color.WHITE, 0, 19);
                    }

                });
    }

    @GameShortcut(key = Input.Keys.F9)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}
