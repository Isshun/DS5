package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.RenderLayer;
import org.smallbox.faraway.core.SpriteModel;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.View;

public class MinimapRenderer extends BaseRenderer {
    private int                 _floor;
    private Sprite              _spriteMap;
    private Color               _color = new Color(0x14dcb9);
    private boolean             _isVisible;
    private View                _panelMain;

    public int getLevel() {
        return MainRenderer.WORLD_RENDERER_LEVEL;
    }

    @Override
    public void onGameStart() {
        _panelMain = UserInterface.getInstance().findById("panel_main");
    }

    public void onRefresh(int frame) {
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

    @Override
    public void onCustomEvent(String tag, Object object) {
        if ("mini_map.display".equals(tag)) {
            _isVisible = (boolean) object;
        }
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    @Override
    protected void onUpdate() {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_panelMain != null && _panelMain.isVisible()) {
            if (_spriteMap == null) {
                createMap();
            }

            if (_spriteMap != null) {
                renderer.draw(_spriteMap, 1210, 84);
            }

            int x = 1210 - (viewport.getPosX() / 32);
            int y = 84 - (viewport.getPosY() / 32);
            renderer.draw(_color, x, y, 32, 1);
            renderer.draw(_color, x, y, 1, 32);
            renderer.draw(_color, x, y + 32, 32, 1);
            renderer.draw(_color, x + 32, y, 1, 32);
        }
    }

    private void createMap() {
        if (GameManager.getInstance().isRunning()) {
            int width = Game.getInstance().getInfo().worldWidth;
            int height = Game.getInstance().getInfo().worldHeight;

            _floor = 9;

            ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (parcels[x][y][_floor].getStructure() != null) {
                        pixmap.drawPixel(x, y, 0x1acb51);
                    } else if (parcels[x][y][_floor].getResource() != null) {
                        pixmap.drawPixel(x, y, 0x14dcb9);
                    } else {
                        pixmap.drawPixel(x, y, 0x0e272f);
                    }
                }
            }

            _spriteMap = new Sprite(new Texture(pixmap));
            _spriteMap.setSize(width, height);
            _spriteMap.setRegion(0, 0, width, height);
            _spriteMap.flip(false, true);
        }
    }
}