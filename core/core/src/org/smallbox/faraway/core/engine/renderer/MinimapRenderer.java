package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.Viewport;
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
    private static final int    COLOR_BACKGROUND = 0xfff9bdff;
    private static final int    COLOR_ROCK = 0x986a50ff;
    private static final int    COLOR_PLANT = 0x9bcd4dff;
    private static final int    COLOR_STRUCTURE = 0x1acb51ff;
    private static final Color  COLOR_CHARACTER = new Color(0xff3c59ff);
    private static final Color  COLOR_VIEW = new Color(0x349394ff);

    private static final float FRAME_WIDTH = 380f;
    private static final float FRAME_HEIGHT = 240f;
    private static final int POS_X = 1210;
    private static final int POS_Y = 84;

    private int                         _floor;
    private Sprite                      _spriteMap;
    private View                        _panelMain;
    private Collection<CharacterModel>  _characters;
    private int                         _width;
    private int                         _height;

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
        _width = Game.getInstance().getInfo().worldWidth;
        _height = Game.getInstance().getInfo().worldHeight;
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
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
                renderer.draw(_spriteMap);
            }

            float ratioX = (FRAME_WIDTH / _width);
            float ratioY = (FRAME_HEIGHT / _height);
            int x = POS_X + (int)((Math.min(_width-32, Math.max(0, -viewport.getPosX() / 32))) * ratioX);
            int y = POS_Y + (int)((Math.min(_height-32, Math.max(0, -viewport.getPosY() / 32))) * ratioY);
            renderer.draw(COLOR_VIEW, x, y, (int) (32 * ratioX), 1);
            renderer.draw(COLOR_VIEW, x, y, 1, (int) (32 * ratioY));
            renderer.draw(COLOR_VIEW, x, (int) (y + 32 * ratioY), (int)(32 * ratioX), 1);
            renderer.draw(COLOR_VIEW, (int) (x + 32 * ratioX), y, 1, (int)(32 * ratioY));

            for (CharacterModel character: _characters) {
                renderer.draw(COLOR_CHARACTER, (int) (POS_X + (character.getParcel().x * ratioX)), (int) (POS_Y + (character.getParcel().y * ratioY)), 2, 2);
            }
        }
    }

    private void createMap() {
        if (GameManager.getInstance().isRunning()) {
            _floor = 9;

            ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
            Pixmap pixmap = new Pixmap(_width, _height, Pixmap.Format.RGB888);
            for (int x = 0; x < _width; x++) {
                for (int y = 0; y < _height; y++) {
                    if (parcels[x][y][_floor].hasStructure()) {
                        pixmap.drawPixel(x, y, COLOR_STRUCTURE);
                    } else if (parcels[x][y][_floor].hasPlant()) {
                        pixmap.drawPixel(x, y, COLOR_PLANT);
                    } else if (parcels[x][y][_floor].hasRock()) {
                        pixmap.drawPixel(x, y, COLOR_ROCK);
                    } else {
                        pixmap.drawPixel(x, y, COLOR_BACKGROUND);
                    }
                }
            }

            _spriteMap = new Sprite(new Texture(pixmap, Pixmap.Format.RGB888, false));
            _spriteMap.setSize(_width, _height);
            _spriteMap.setRegion(0, 0, _width, _height);
            _spriteMap.flip(false, true);
            _spriteMap.setScale(FRAME_WIDTH / _width, FRAME_HEIGHT / _height);
            _spriteMap.setPosition(POS_X, POS_Y);
            _spriteMap.setOrigin(0, 0);
        }
    }
}