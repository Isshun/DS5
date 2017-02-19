package org.smallbox.faraway.client.debug;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL + 1)
public class DebugGroundRenderer extends BaseRenderer {

    @BindModule
    private WorldModule worldModule;

    @Override
    public void onGameCreate(Game game) {
        setVisibility(false);
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        worldModule.getParcelList().stream()
//                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> {
                    if (parcel.getGroundInfo() != null) {
                        renderer.drawOnMap(parcel.x, parcel.y, Color.CORAL);

                        renderer.drawOnMap(parcel.x, parcel.y, parcel.getGroundInfo().label, 14, Color.BLACK, 1, 1);
                        renderer.drawOnMap(parcel.x, parcel.y, parcel.getGroundInfo().label, 14, Color.WHITE);

                        renderer.drawOnMap(parcel.x, parcel.y, String.valueOf(parcel.z), 14, Color.BLACK, 1, 17);
                        renderer.drawOnMap(parcel.x, parcel.y, String.valueOf(parcel.z), 14, Color.WHITE, 0, 16);
                    }

                });
    }

    @GameShortcut(key = GameEventListener.Key.F9)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}
