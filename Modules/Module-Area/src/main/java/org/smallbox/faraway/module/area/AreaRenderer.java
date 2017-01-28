package org.smallbox.faraway.module.area;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.dependencyInjector.BindManager;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.GameDisplay;
import org.smallbox.faraway.client.renderer.SpriteManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaRenderer extends GameDisplay {

    @BindManager
    private SpriteManager spriteManager;

    private final TextureRegion[] _regions;
    private final TextureRegion[] _regionsSelected;

    private Color[] COLORS = new Color[]{
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    public AreaRenderer() {
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 0, 32, 32);
        _regions[1] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 32, 32, 32);
        _regions[2] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 64, 32, 32);
        _regions[3] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 96, 32, 32);
        _regions[4] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 128, 32, 32);
        _regionsSelected = new TextureRegion[5];
        _regionsSelected[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 0, 32, 32);
        _regionsSelected[1] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 32, 32, 32);
        _regionsSelected[2] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 64, 32, 32);
        _regionsSelected[3] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 96, 32, 32);
        _regionsSelected[4] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 128, 32, 32);
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = -viewport.getPosX() / Constant.TILE_WIDTH;
        int fromY = -viewport.getPosY() / Constant.TILE_HEIGHT;
        int toX = fromX + viewport.getWidth() / Constant.TILE_WIDTH;
        int toY = fromY + viewport.getHeight() / Constant.TILE_HEIGHT;

        // TODO
//        WorldModule world = (WorldModule) Application.moduleManager.getModule(WorldModule.class);
//        for (int x = fromX; x < toX; x++) {
//            for (int y = fromY; y < toY; y++) {
//                ParcelModel parcel = world.getParcel(x, y, WorldHelper.getCurrentFloor());
//                if (parcel != null && parcel.getArea() != null) {
//                    if (Application.gameManager.getGame().getSelector().getSelectedArea() == parcel.getArea()) {
//                        renderer.drawOnMap(_regionsSelected[Math.min(parcel.getArea().getTypeIndex(), 4)], x, y);
//                    } else {
//                        renderer.drawOnMap(_regions[Math.min(parcel.getArea().getTypeIndex(), 4)], x, y);
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public String getName() {
        return "areas";
    }

    public boolean isMandatory() {
        return true;
    }
}
