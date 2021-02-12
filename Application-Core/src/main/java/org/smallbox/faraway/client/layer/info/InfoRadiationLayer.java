package org.smallbox.faraway.client.layer.info;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.RadiationModule;
import org.smallbox.faraway.game.world.WorldModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.INFO_LEVEL, visible = false)
public class InfoRadiationLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;
    @Inject private RadiationModule radiationModule;

    private final Color color = new Color(0x34e13d88);

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getAll().stream()
                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> {
                    color.a = radiationModule.getLevel(parcel);
                    renderer.drawRectangleOnMap(parcel, TILE_SIZE, TILE_SIZE, color, 0, 0);
                });
    }

    @GameShortcut("info/radiation")
    public void display() {
        toggleVisibility();
    }

}
