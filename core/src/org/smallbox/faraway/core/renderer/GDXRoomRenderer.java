package org.smallbox.faraway.core.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.world.RoomManager;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Constant;

import java.util.Random;

/**
 * Created by Alex on 17/06/2015.
 */
public class GDXRoomRenderer extends BaseRenderer {
    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        ((RoomManager)Game.getInstance().getManager(RoomManager.class)).getRoomList().stream().forEach(room -> {
            for (ParcelModel parcel : room.getParcels()) {
                Random random = new Random(room.getId());
                renderer.draw(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f),
                        (int) ((parcel.x * Constant.TILE_WIDTH + viewport.getPosX()) * viewport.getScale()),
                        (int) ((parcel.y * Constant.TILE_HEIGHT + viewport.getPosY()) * viewport.getScale()),
                        32,
                        32);
            }
        });
    }

    @Override
    public void onRefresh(int frame) {

    }
}
