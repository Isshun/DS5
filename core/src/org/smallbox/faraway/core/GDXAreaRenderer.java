package org.smallbox.faraway.core;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.game.model.area.GardenAreaModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.manager.AreaManager;
import org.smallbox.faraway.game.model.area.AreaModel;

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
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        AreaManager areaManager = (AreaManager) Game.getInstance().getManager(AreaManager.class);
        for (AreaModel area: areaManager.getAreas()) {
            for (ParcelModel parcel: area.getParcels()) {
                if (area instanceof GardenAreaModel || area.isStorage() || UserInterface.getInstance().getMode() == UserInterface.Mode.AREA) {
                    ((GDXRenderer) renderer).draw(COLORS[area.getType().ordinal()],
                            (int) ((parcel.x * Constant.TILE_WIDTH + effect.getViewport().getPosX()) * effect.getViewport().getScale()),
                            (int) ((parcel.y * Constant.TILE_HEIGHT + effect.getViewport().getPosY()) * effect.getViewport().getScale()),
                            (int) (32 * effect.getViewport().getScale()),
                            (int) (32 * effect.getViewport().getScale()));
                }
            }
        }
    }

    @Override
    public void onRefresh(int frame) {

    }
}
