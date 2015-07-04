package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;

public class DebugRenderer extends BaseRenderer {
	private RenderLayer 	_cache;

	public DebugRenderer() {
        _cache = ViewFactory.getInstance().createRenderLayer(0, 0, 0);
	}

    @Override
    public void onRefresh(int frame) {
//        UILabel lbDebug = ViewFactory.getInstance().createTextView();
//        lbDebug.setCharacterSize(12);
//
//        _cache.begin();
//
//        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
//
//            lbDebug.setString(String.valueOf(parcel.getLight()));
//            lbDebug.setCharacterSize(14);
//            lbDebug.setColor(Color.WHITE);
////            lbDebug.setPosition((int) (parcel.getX() * Constant.TILE_WIDTH), (int) (parcel.getY() * Constant.TILE_HEIGHT));
//            lbDebug.setPosition(0, 0);
//            _cache.draw(lbDebug);
//        }
//        _cache.end();
    }

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        UILabel lbDebug = ViewFactory.getInstance().createTextView();
        lbDebug.setCharacterSize(14);
        lbDebug.setColor(Color.WHITE);
        lbDebug.setPosition(0, 0);
        lbDebug.setSize(32, 32);
        lbDebug.setAlign(View.Align.CENTER);

        int relX = UserInterface.getInstance().getMouseX();
        int relY = UserInterface.getInstance().getMouseY();

        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
            if (parcel.getZ() == 0 && parcel.getX() > relX - 8 && parcel.getX() < relX + 8 && parcel.getY() > relY - 8 && parcel.getY() < relY + 8) {
                lbDebug.setString(String.valueOf((int)(parcel.getLight() * 10)));
//                lbDebug.setString(parcel.getX() + "x" + parcel.getY());
                renderer.draw(lbDebug, (int) (parcel.getX() * Constant.TILE_WIDTH + effect.getViewport().getPosX()), (int) (parcel.getY() * Constant.TILE_HEIGHT + effect.getViewport().getPosY()));
            }
        }
	}

}