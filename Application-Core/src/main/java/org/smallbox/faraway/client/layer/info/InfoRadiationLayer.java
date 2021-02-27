package org.smallbox.faraway.client.layer.info;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.RadiationModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.INFO_LEVEL, visible = false)
public class InfoRadiationLayer extends BaseMapLayer {
    @Inject private RadiationModule radiationModule;

    private final Color color = new Color(0x34e13d88);

    @Override
    protected void onDrawParcel(BaseRenderer renderer, Parcel parcel) {
        color.a = radiationModule.getLevel(parcel);
        renderer.drawRectangleOnMap(parcel, TILE_SIZE, TILE_SIZE, color, 0, 0);
    }

    @GameShortcut("info/radiation")
    public void display() {
        toggleVisibility();
    }

}
