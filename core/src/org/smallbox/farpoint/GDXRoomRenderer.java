package org.smallbox.farpoint;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 17/06/2015.
 */
public class GDXRoomRenderer extends BaseRenderer {
    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        Game.getRoomManager().getRoomList().stream().filter(room -> !room.isExterior()).forEach(room -> {
            for (ParcelModel parcel : room.getParcels()) {
                ((GDXRenderer) renderer).draw(Color.BLUE,
                        parcel.getX() * Constant.TILE_WIDTH + effect.getViewport().getPosX(),
                        parcel.getY() * Constant.TILE_HEIGHT + effect.getViewport().getPosY(),
                        32,
                        32);
            }
        });
    }

    @Override
    public void onRefresh(int frame) {

    }

    @Override
    public void invalidate(int x, int y) {

    }

    @Override
    public void invalidate() {

    }
}
