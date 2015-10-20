package org.smallbox.faraway.renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.game.model.room.RoomModel;
import org.smallbox.faraway.core.game.module.ModuleManager;
import org.smallbox.faraway.core.game.module.base.RoomModule;
import org.smallbox.faraway.core.util.Constant;

/**
 * Created by Alex on 14/06/2015.
 */
public class GDXTemperatureRenderer extends BaseRenderer {
    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        RoomModule roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
        for (RoomModel room: roomModule.getRoomList()) {
            if (!room.isExterior()) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                float offset = (float) (Math.max(-50, Math.min(50, 21 - room.getTemperatureInfo().temperature)) / 50f);
                Color color = offset > 0 ? new Color(1f - offset, 1f - offset, 1f, 0.8f) : new Color(1f, 1f - offset, 1f - offset, 0.8f);
                for (ParcelModel parcel : room.getParcels()) {
                    renderer.draw(color,
                            parcel.x * Constant.TILE_WIDTH + viewport.getPosX(),
                            parcel.y * Constant.TILE_HEIGHT + viewport.getPosY(),
                            32,
                            32);
                    if (parcel.x > maxX) maxX = parcel.x;
                    if (parcel.y > maxY) maxY = parcel.y;
                    if (parcel.x < minX) minX = parcel.x;
                    if (parcel.y < minY) minY = parcel.y;
                }

                String text = room.getTemperatureInfo().temperature + "°";
                BitmapFont.TextBounds bounds = renderer.getFont(24).getWrappedBounds(text, 0);
                renderer.draw(text, 24,
                        (minX + (maxX - minX) / 2) * Constant.TILE_WIDTH + viewport.getPosX(),
                        (minY + (maxY - minY) / 2) * Constant.TILE_HEIGHT + viewport.getPosY(),
                        Color.BLACK);
            }
        }
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.temperature;
    }
}
