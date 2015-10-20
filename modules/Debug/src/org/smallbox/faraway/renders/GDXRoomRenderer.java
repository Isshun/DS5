package org.smallbox.faraway.renders;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.game.module.ModuleManager;
import org.smallbox.faraway.core.game.module.base.RoomModule;
import org.smallbox.faraway.core.util.Constant;

import java.util.Random;

/**
 * Created by Alex on 17/06/2015.
 */
public class GDXRoomRenderer extends BaseRenderer {
    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        ((RoomModule) ModuleManager.getInstance().getModule(RoomModule.class)).getRoomList().stream().forEach(room -> {
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

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.room;
    }
}
