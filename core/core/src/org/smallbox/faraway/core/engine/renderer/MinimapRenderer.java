package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.Collection;

public class MinimapRenderer extends BaseRenderer {
//    private static final int    COLOR_BACKGROUND = 0xfff9bdff;
    private static final int    COLOR_ROCK = 0x60442dff;
    private static final int    COLOR_PLANT = 0x9bcd4dff;
    private static final int    COLOR_STRUCTURE = 0x333333ff;
    private static final Color  COLOR_CHARACTER = new Color(0xff3c59ff);
    private static final Color  COLOR_VIEW = new Color(0x349394ff);
    private static final Color  COLOR_WATER = new Color(0x006d7c1d);

    private static final int    FRAME_WIDTH = 352;
    private static final int    FRAME_HEIGHT = 220;
    private static int          POS_X;
    private static int          POS_Y;

    private int                         _floor;
    private Sprite                      _spriteMap;
    private View                        _panelMain;
    private Collection<CharacterModel>  _characters;
    private int                         _width;
    private int                         _height;
    private boolean                     _dirty;
    private Pixmap                      _pixmap;

    public int getLevel() {
        return MainRenderer.WORLD_GROUND_RENDERER_LEVEL;
    }

    @Override
    public void onGameStart() {
        _panelMain = UserInterface.getInstance().findById("base.ui.panel_main");
    }

    @Override
    public void onReloadUI() {
        _panelMain = UserInterface.getInstance().findById("base.ui.panel_main");
    }

    @Override
    protected void onLoad(Game game) {
        POS_X = (int) (Gdx.graphics.getWidth() - FRAME_WIDTH * Application.getInstance().getConfig().uiScale - 10 * Application.getInstance().getConfig().uiScale);
        POS_Y = (int) (84 * Application.getInstance().getConfig().uiScale);
        _characters = ModuleHelper.getCharacterModule().getCharacters();
        _width = Game.getInstance().getInfo().worldWidth;
        _height = Game.getInstance().getInfo().worldHeight;

        _pixmap = new Pixmap(_width, _height, Pixmap.Format.RGB888);
        _floor = WorldHelper.getCurrentFloor();
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
        _dirty = true;
    }

    @Override
    public void onStructureComplete(StructureModel structure) {
        _dirty = true;
    }

    @Override
    public void onItemComplete(ItemModel item) {
        _dirty = true;
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
        _dirty = true;
    }

    @Override
    protected void onUpdate() {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_panelMain != null && _panelMain.isVisible()) {
            int width = (int) (FRAME_WIDTH * Application.getInstance().getConfig().uiScale);
            int height = (int) (FRAME_HEIGHT * Application.getInstance().getConfig().uiScale);

            if (_dirty || _spriteMap == null) {
                _dirty = false;
                createMap(width, height);
            }

            if (_spriteMap != null) {
                renderer.draw(_spriteMap);
            }

            float ratioX = ((float)width / _width);
            float ratioY = ((float)height / _height);
            int x = POS_X + (int)((Math.min(_width-38-1, Math.max(0, -viewport.getPosX() / 32))) * ratioX);
            int y = POS_Y + (int)((Math.min(_height-32-1, Math.max(0, -viewport.getPosY() / 32))) * ratioY);
            renderer.draw(COLOR_VIEW, x, y, (int) (38 * ratioX), 1);
            renderer.draw(COLOR_VIEW, x, y, 1, (int) (32 * ratioY));
            renderer.draw(COLOR_VIEW, x, (int) (y + 32 * ratioY), (int)(38 * ratioX), 1);
            renderer.draw(COLOR_VIEW, (int) (x + 38 * ratioX), y, 1, (int)(32 * ratioY) + 1);

            for (CharacterModel character: _characters) {
                if (character.getParcel().z == WorldHelper.getCurrentFloor()) {
                    renderer.draw(COLOR_CHARACTER, (int) (POS_X + (character.getParcel().x * ratioX)), (int) (POS_Y + (character.getParcel().y * ratioY)), 3, 3);
                }
            }
        }
    }

    private void createMap(int width, int height) {
        if (GameManager.getInstance().isLoaded()) {
            ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
            for (int x = 0; x < _width; x++) {
                for (int y = 0; y < _height; y++) {
                    if (parcels[x][y][_floor].hasStructure()) {
                        _pixmap.drawPixel(x, y, COLOR_STRUCTURE);
                    } else if (parcels[x][y][_floor].hasPlant()) {
                        _pixmap.drawPixel(x, y, COLOR_PLANT);
                    } else if (parcels[x][y][_floor].hasRock()) {
                        _pixmap.drawPixel(x, y, COLOR_ROCK);
                    } else if (parcels[x][y][_floor].hasGround()) {
                        _pixmap.drawPixel(x, y, parcels[x][y][_floor].getGroundInfo().color);
                    } else if (parcels[x][y][_floor].hasLiquid()) {
                        _pixmap.drawPixel(x, y, parcels[x][y][_floor].getLiquidInfo().color);
                    } else {
                        _pixmap.drawPixel(x, y, 0x000000);
                    }
                }
            }
            _spriteMap = new Sprite(new Texture(_pixmap, Pixmap.Format.RGB888, false));
            _spriteMap.setSize(_width, _height);
            _spriteMap.setRegion(0, 0, _width, _height);
            _spriteMap.flip(false, true);
            _spriteMap.setScale((float)width / _width, (float)height / _height);
            _spriteMap.setPosition(POS_X, POS_Y);
            _spriteMap.setOrigin(0, 0);
        }
    }
}