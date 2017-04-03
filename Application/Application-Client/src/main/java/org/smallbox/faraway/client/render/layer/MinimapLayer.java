package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.world.WorldModule;

@GameLayer(level = LayerManager.MINI_MAP_LEVEL, visible = true)
public class MinimapLayer extends BaseLayer {
    //    private static final int    COLOR_background = 0xfffff9bdff;
    private static final int    COLOR_ROCK = 0x60442dff;
    private static final int    COLOR_PLANT = 0x9bcd4dff;
    private static final int    COLOR_STRUCTURE = 0x333333ff;
    private static final Color  COLOR_CHARACTER = new Color(0xff3c59ff);
    private static final Color  COLOR_VIEW = new Color(0x349394ff);
    private static final Color  COLOR_WATER = new Color(0x006d7c1d);

    private static final int    FRAME_WIDTH = 352;
    private static final int    FRAME_HEIGHT = 220;

    private int                         _mainPosX;
    private int                         _mainPosY;
    private int                         _floor;
    private Sprite                      _spriteMap;
    private View                        _panelMain;
    private int                         _width;
    private int                         _height;
    private boolean                     _dirty;
    private Pixmap                      _pixmap;

    @BindModule
    private PlantModule plantModule;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private CharacterModule characterModule;

    @Override
    public void onGameStart(Game game) {
        _mainPosX = (int) (Gdx.graphics.getWidth() - FRAME_WIDTH * Application.config.uiScale - 10 * Application.config.uiScale);
        _mainPosY = (int) (84 * Application.config.uiScale);

        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        _pixmap = new Pixmap(_width, _height, Pixmap.Format.RGB888);
        _floor = WorldHelper.getCurrentFloor();
    }

    @Override
    public void onReloadUI() {
        _panelMain = ApplicationClient.uiManager.findById("base.ui.right_panel.content");
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
        _dirty = true;
    }

    // TODO
//    @Override
//    public void onStructureComplete(StructureItem structure) {
//        _dirty = true;
//    }
//
//    @Override
//    public void onItemComplete(UsableItem item) {
//        _dirty = true;
//    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
        _dirty = true;
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (_panelMain != null && _panelMain.isVisible()) {
            int width = (int) (FRAME_WIDTH * Application.config.uiScale);
            int height = (int) (FRAME_HEIGHT * Application.config.uiScale);

            if (_dirty || _spriteMap == null) {
                _dirty = false;
                createMap(width, height);
            }

            if (_spriteMap != null) {
                renderer.draw(_spriteMap);
            }

            float ratioX = ((float)width / _width);
            float ratioY = ((float)height / _height);
            int x = _mainPosX + (int)((Math.min(_width-38-1, Math.max(0, -viewport.getPosX() / 32))) * ratioX);
            int y = _mainPosY + (int)((Math.min(_height-32-1, Math.max(0, -viewport.getPosY() / 32))) * ratioY);
            renderer.drawPixel(x, y, (int) (38 * ratioX), 1, COLOR_VIEW);
            renderer.drawPixel(x, y, 1, (int) (32 * ratioY), COLOR_VIEW);
            renderer.drawPixel(x, (int) (y + 32 * ratioY), (int)(38 * ratioX), 1, COLOR_VIEW);
            renderer.drawPixel((int) (x + 38 * ratioX), y, 1, (int)(32 * ratioY) + 1, COLOR_VIEW);

            characterModule.getCharacters().stream()
                    .filter(character -> character.getParcel().z == WorldHelper.getCurrentFloor())
                    .forEach(character -> renderer.drawPixel((int) (_mainPosX + (character.getParcel().x * ratioX)), (int) (_mainPosY + (character.getParcel().y * ratioY)), 3, 3, COLOR_CHARACTER
                    ));
        }
    }

    private void createMap(int width, int height) {
        if (Application.gameManager.isLoaded()) {

            ParcelModel[][][] parcels = worldModule.getParcels();
            for (int x = 0; x < _width; x++) {
                for (int y = 0; y < _height; y++) {
                    if (parcels[x][y][_floor].hasItem(StructureItem.class)) {
                        _pixmap.drawPixel(x, y, COLOR_STRUCTURE);
                    } else if (plantModule.getPlant(parcels[x][y][_floor]) != null) {
                        _pixmap.drawPixel(x, y, COLOR_PLANT);
                    } else if (parcels[x][y][_floor].hasRock()) {
                        _pixmap.drawPixel(x, y, COLOR_ROCK);
                    } else if (parcels[x][y][_floor].hasGround()) {
                        _pixmap.drawPixel(x, y, parcels[x][y][_floor].getGroundInfo().color);
                    } else if (parcels[x][y][_floor].hasLiquid()) {
                        _pixmap.drawPixel(x, y, parcels[x][y][_floor].getLiquidInfo().color);
                    } else {
                        _pixmap.drawPixel(x, y, 0xff000000);
                    }
                }
            }

            _spriteMap = new Sprite(new Texture(_pixmap, Pixmap.Format.RGB888, false));
            _spriteMap.setSize(_width, _height);
            _spriteMap.setRegion(0, 0, _width, _height);
            _spriteMap.flip(false, true);
            _spriteMap.setScale((float)width / _width, (float)height / _height);
            _spriteMap.setPosition(_mainPosX, _mainPosY);
            _spriteMap.setOrigin(0, 0);
        }
    }
}