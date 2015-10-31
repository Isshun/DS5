package org.smallbox.faraway.renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.core.util.Constant;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaRenderer extends BaseRenderer {
    private final SpriteManager _spriteManager;
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
        _spriteManager = SpriteManager.getInstance();
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 0, 32, 32);
        _regions[1] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 32, 32, 32);
        _regions[2] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 64, 32, 32);
        _regions[3] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 96, 32, 32);
        _regions[4] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 128, 32, 32);
        _regionsSelected = new TextureRegion[5];
        _regionsSelected[0] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 0, 32, 32);
        _regionsSelected[1] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 32, 32, 32);
        _regionsSelected[2] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 64, 32, 32);
        _regionsSelected[3] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 96, 32, 32);
        _regionsSelected[4] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 128, 32, 32);
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = -viewport.getPosX() / Constant.TILE_WIDTH;
        int fromY = -viewport.getPosY() / Constant.TILE_HEIGHT;
        int toX = fromX + viewport.getWidth() / Constant.TILE_WIDTH;
        int toY = fromY + viewport.getHeight() / Constant.TILE_HEIGHT;

        WorldModule world = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                ParcelModel parcel = world.getParcel(x, y);
                if (parcel != null && parcel.getArea() != null) {
                    if (UserInterface.getInstance().getSelector().getSelectedArea() == parcel.getArea()) {
                        renderer.drawOnMap(_regionsSelected[Math.min(parcel.getArea().getTypeIndex(), 4)], x, y);
                    } else {
                        renderer.drawOnMap(_regions[Math.min(parcel.getArea().getTypeIndex(), 4)], x, y);
                    }
                }
            }
        }
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.area;
    }
}
