package org.smallbox.farpoint;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.AreaManager;
import org.smallbox.faraway.ui.AreaModel;

/**
 * Created by Alex on 13/06/2015.
 */
public class GDXAreaRenderer extends BaseRenderer {
    private Color COLOR = new Color(1, 1, 0, 0.4f);

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
//        for (int x = 0; x < Game.getWorldManager().getWidth(); x++) {
//            for (int y = 0; y < Game.getWorldManager().getHeight(); y++) {
//                if (Game.getWorldManager().getParcel(x, y).getElevation() > 1) {
//                    ((GDXRenderer) renderer).draw(new Color(1f, 1f, 35 / 100f, 1f),
//                            x * Constant.TILE_WIDTH + effect.getViewport().getPosX(),
//                            y * Constant.TILE_HEIGHT + effect.getViewport().getPosY(),
//                            32,
//                            32);
//                } else {
//                    ((GDXRenderer) renderer).draw(new Color(1f, 1f, 1f, 1f),
//                            x * Constant.TILE_WIDTH + effect.getViewport().getPosX(),
//                            y * Constant.TILE_HEIGHT + effect.getViewport().getPosY(),
//                            32,
//                            32);
//                }
//            }
//
//        }

//        AreaManager areaManager = Game.getAreaManager();
//        for (AreaModel area: areaManager.getAreas()) {
//            for (ParcelModel parcel: area.getParcels()) {
//                ((GDXRenderer)renderer).draw(COLOR,
//                        parcel.getX() * Constant.TILE_WIDTH + effect.getViewport().getPosX(),
//                        parcel.getY() * Constant.TILE_HEIGHT + effect.getViewport().getPosY(),
//                        32,
//                        32);
//            }
//        }
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
