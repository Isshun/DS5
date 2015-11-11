package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.Collection;

public class MinimapRenderer extends BaseRenderer {
    private static final Color COLOR_BACKGROUND = new Color(0x000000);
    private static final Color COLOR_ROCK = new Color(0x14dcb9);
    private static final Color COLOR_PLANT = new Color(0x14fcb9);
    private static final Color COLOR_STRUCTURE = new Color(0x1acb51);
    private static final Color COLOR_CHARACTER = new Color(0xff5c89);
    private static final Color COLOR_VIEW = new Color(0x14dcb9);

    private static final int POS_X = 1210;
    private static final int POS_Y = 84;

    private int                 _floor;
    private Sprite              _spriteMap;
    private boolean             _isVisible;
    private View                _panelMain;
    private Collection<CharacterModel> _characters;

    public int getLevel() {
        return MainRenderer.WORLD_RENDERER_LEVEL;
    }

    @Override
    public void onGameStart() {
        _panelMain = UserInterface.getInstance().findById("panel_main");
    }

    @Override
    protected void onLoad(Game game) {
        _characters = ModuleHelper.getCharacterModule().getCharacters();
    }

    @Override
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
                renderer.draw(_spriteMap, POS_X, POS_Y);
            }

            int x = POS_X - (viewport.getPosX() / 32);
            int y = POS_Y - (viewport.getPosY() / 32);
            renderer.draw(COLOR_VIEW, x, y, 32, 1);
            renderer.draw(COLOR_VIEW, x, y, 1, 32);
            renderer.draw(COLOR_VIEW, x, y + 32, 32, 1);
            renderer.draw(COLOR_VIEW, x + 32, y, 1, 32);

            for (CharacterModel character: _characters) {
                renderer.draw(COLOR_CHARACTER, POS_X + character.getParcel().x, POS_Y + character.getParcel().y, 2, 2);
            }
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
                    if (parcels[x][y][_floor].hasStructure()) {
                        pixmap.drawPixel(x, y, (int) COLOR_STRUCTURE.toLong());
                    } else if (parcels[x][y][_floor].hasPlant()) {
                        pixmap.drawPixel(x, y, (int) COLOR_PLANT.toLong());
                    } else if (parcels[x][y][_floor].hasRock()) {
                        pixmap.drawPixel(x, y, (int) COLOR_ROCK.toLong());
                    } else {
                        pixmap.drawPixel(x, y, (int) COLOR_BACKGROUND.toLong());
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