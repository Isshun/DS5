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
import org.smallbox.faraway.game.room.RoomModule;
import org.smallbox.faraway.game.world.WorldModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class InfoRoomLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;
    @Inject private RoomModule roomModule;

    private Color color = new Color(0xff000088);

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        roomModule.getRoomMap().forEach((baseParcel, room) -> {
            room.getParcels().forEach(parcel -> {
                renderer.drawRectangleOnMap(parcel, TILE_SIZE, TILE_SIZE, color, 0, 0);
            });
        });
    }

    @GameShortcut("info/rooms")
    public void display() {
        toggleVisibility();
    }

}
