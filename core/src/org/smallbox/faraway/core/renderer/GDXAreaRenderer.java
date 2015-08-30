package org.smallbox.faraway.core.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.model.GameConfig;

/**
 * Created by Alex on 13/06/2015.
 */
public class GDXAreaRenderer extends BaseRenderer {
    private Color[] COLORS = new Color[]{
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
//        AreaModule areaManager = (AreaModule) Game.getInstance().getModule(AreaModule.class);
//        for (AreaModel area: areaManager.getAreas()) {
//            for (ParcelModel parcel: area.getParcels()) {
//                if (area instanceof GardenAreaModel || area.isStorage() || UserInterface.getInstance().getMode() == UserInterface.Mode.AREA) {
//                    ((GDXRenderer) renderer).draw(COLORS[area.getType().ordinal()],
//                            (int) ((parcel.x * Constant.TILE_WIDTH + effect.getViewport().getPosX()) * effect.getViewport().getScale()),
//                            (int) ((parcel.y * Constant.TILE_HEIGHT + effect.getViewport().getPosY()) * effect.getViewport().getScale()),
//                            (int) (32 * effect.getViewport().getScale()),
//                            (int) (32 * effect.getViewport().getScale()));
//                }
//            }
//        }
    }

    @Override
    public void onRefresh(int frame) {

    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.area;
    }
}
