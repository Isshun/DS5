package org.smallbox.faraway.client.debug.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL + 1, visible = false)
public class DebugGroundRenderer extends BaseRenderer {

    @BindModule
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

    @GameShortcut(key = GameEventListener.Key.F9)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}
