package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.util.Constant;

public class DebugRenderer extends BaseRenderer {

	public DebugRenderer() {
//        _cache = ViewFactory.getInstance().createRenderLayer(0, 0, 0);
	}

    @Override
    public void onRefresh(int frame) {
//        UILabel lbDebug = ViewFactory.getInstance().createTextView();
//        lbDebug.setTextSize(12);
//
//        _cache.begin();
//
//        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
//
//            lbDebug.setText(String.valueOf(parcel.getLight()));
//            lbDebug.setTextSize(14);
//            lbDebug.setColor(Color.WHITE);
////            lbDebug.setPosition((int) (parcel.x * Constant.TILE_WIDTH), (int) (parcel.y * Constant.TILE_HEIGHT));
//            lbDebug.setPosition(0, 0);
//            _cache.draw(lbDebug);
//        }
//        _cache.end();
    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.debug;
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        UILabel lbDebug = ViewFactory.getInstance().createTextView();
        lbDebug.setTextSize(14);
        lbDebug.setColor(Color.WHITE);
        lbDebug.setPosition(0, 0);
        lbDebug.setSize(32, 32);
        lbDebug.setTextAlign(View.Align.CENTER);

        int relX = UserInterface.getInstance().getMouseX();
        int relY = UserInterface.getInstance().getMouseY();

        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
            if (parcel.z == 0) {
                Color color = new Color(parcel.getResource().getQuantity() * 250 / 1300, 0, 0);
                renderer.draw(color, (int) (parcel.x * Constant.TILE_WIDTH + viewport.getPosX()), (int) (parcel.y * Constant.TILE_HEIGHT + viewport.getPosY()), 32, 32);
            }
        }

//        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
//            if (parcel.getZ() == 0 && parcel.x > relX - 8 && parcel.x < relX + 8 && parcel.y > relY - 8 && parcel.y < relY + 8) {
//                lbDebug.setText(String.valueOf((int)(parcel.getLight() * 10)));
////                lbDebug.setText(parcel.x + "x" + parcel.y);
//                renderer.draw(lbDebug, (int) (parcel.x * Constant.TILE_WIDTH + effect.getViewport().getPosX()), (int) (parcel.y * Constant.TILE_HEIGHT + effect.getViewport().getPosY()));
//            }
//        }
	}

}