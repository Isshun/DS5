package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugGroundLayer extends BaseLayer {

    @Inject
    private WorldModule worldModule;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getParcelList().stream()
                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> {
                    if (parcel.getGroundInfo() != null) {
                        renderer.drawOnMap(parcel.x, parcel.y, Color.CORAL);

                        renderer.drawTextOnMap(parcel, parcel.getGroundInfo().label, 14, Color.BLACK, 1, 1);
                        renderer.drawTextOnMap(parcel, parcel.getGroundInfo().label, 14, Color.WHITE);

                        renderer.drawTextOnMap(parcel, String.valueOf(parcel.z), 14, Color.BLACK, 1, 17);
                        renderer.drawTextOnMap(parcel, String.valueOf(parcel.z), 14, Color.WHITE, 0, 16);
                    }

                });
    }

    @GameShortcut(key = Input.Keys.F9)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}
