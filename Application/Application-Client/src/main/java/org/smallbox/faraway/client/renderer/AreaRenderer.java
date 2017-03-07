package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 13/06/2015.
 */
@GameRenderer(level = 0, visible = true)
public class AreaRenderer extends BaseRenderer {

    @BindComponent
    private SpriteManager spriteManager;

    @BindModule
    private AreaModule areaModule;

    private TextureRegion[] _regions;
    private TextureRegion[] _regionsSelected;

    private Color[] COLORS = new Color[]{
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    @Override
    public void onGameStart(Game game) {
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
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = -viewport.getPosX() / Constant.TILE_WIDTH;
        int fromY = -viewport.getPosY() / Constant.TILE_HEIGHT;
        int toX = fromX + viewport.getWidth() / Constant.TILE_WIDTH;
        int toY = fromY + viewport.getHeight() / Constant.TILE_HEIGHT;

        areaModule.getAreas().forEach(area -> {
            area.getParcels().forEach(parcel -> {
//                renderer.drawRectangleOnMap(parcel.x, parcel.y, 32, 32, Color.BLUE, true, 0, 0);
                renderer.drawOnMap(parcel.x, parcel.y, _regions[Math.min(parcel.getArea().getTypeIndex(), 4)]);
            });
        });

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

    public boolean isMandatory() {
        return true;
    }
}
