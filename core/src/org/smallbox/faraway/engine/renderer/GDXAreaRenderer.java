package org.smallbox.faraway.engine.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.module.world.WorldModule;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 13/06/2015.
 */
public class GDXAreaRenderer extends BaseRenderer {
    private final SpriteManager _spriteManager;

    private final TextureRegion[] _regions;

    private Color[] COLORS = new Color[]{
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    public GDXAreaRenderer() {
        _spriteManager = SpriteManager.getInstance();
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 0, 32, 32);
        _regions[1] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 32, 32, 32);
        _regions[2] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 64, 32, 32);
        _regions[3] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 96, 32, 32);
        _regions[4] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 128, 32, 32);
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = -viewport.getPosX() / Constant.TILE_WIDTH;
        int fromY = -viewport.getPosY() / Constant.TILE_HEIGHT;
        int toX = fromX + viewport.getWidth() / Constant.TILE_WIDTH;
        int toY = fromY + viewport.getHeight() / Constant.TILE_HEIGHT;

        WorldModule world = (WorldModule) Game.getInstance().getModule(WorldModule.class);
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                ParcelModel parcel = world.getParcel(x, y);
                if (parcel != null && parcel.getArea() != null) {
                    renderer.drawOnMap(_regions[Math.min(parcel.getArea().getTypeIndex(), 4)], x, y);
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
