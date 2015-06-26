package org.smallbox.farpoint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.room.RoomModel;

/**
 * Created by Alex on 14/06/2015.
 */
public class GDXTemperatureRenderer extends BaseRenderer {
    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        RoomManager roomManager = (RoomManager) Game.getInstance().getManager(RoomManager.class);
        for (RoomModel room: roomManager.getRoomList()) {
            if (!room.isExterior()) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                float offset = (float) (Math.max(-50, Math.min(50, 21 - room.getTemperatureInfo().temperature)) / 50f);
                Color color = offset > 0 ? new Color(1f - offset, 1f - offset, 1f, 0.8f) : new Color(1f, 1f - offset, 1f - offset, 0.8f);
                for (ParcelModel parcel : room.getParcels()) {
                    ((GDXRenderer) renderer).draw(color,
                            parcel.getX() * Constant.TILE_WIDTH + effect.getViewport().getPosX(),
                            parcel.getY() * Constant.TILE_HEIGHT + effect.getViewport().getPosY(),
                            32,
                            32);
                    if (parcel.getX() > maxX) maxX = parcel.getX();
                    if (parcel.getY() > maxY) maxY = parcel.getY();
                    if (parcel.getX() < minX) minX = parcel.getX();
                    if (parcel.getY() < minY) minY = parcel.getY();
                }

                String text = room.getTemperatureInfo().temperature + "°";
                BitmapFont.TextBounds bounds = ((GDXRenderer)renderer).getFont(24).getWrappedBounds(text, 0);
                ((GDXRenderer) renderer).draw(text, 24,
                        (minX + (maxX - minX) / 2) * Constant.TILE_WIDTH + effect.getViewport().getPosX(),
                        (minY + (maxY - minY) / 2) * Constant.TILE_HEIGHT + effect.getViewport().getPosY(),
                        Color.BLACK);
            }
        }
    }

    @Override
    public void onRefresh(int frame) {

    }
}
