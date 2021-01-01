package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.world.WorldModule;

@GameObject
@GameLayer(level = LayerManager.MINI_MAP_LEVEL, visible = true)
public class MinimapLayer extends BaseLayer {
    //    private static final int    COLOR_background = 0xfffff9bdff;
    private static final int    COLOR_ROCK = 0x60442dff;
    private static final int    COLOR_PLANT = 0x9bcd4dff;
    private static final int    COLOR_STRUCTURE = 0x333333ff;
    private static final Color  COLOR_ITEM = new Color(0xff3333ff);
    private static final Color  COLOR_CHARACTER = new Color(0x3c59ffff);
    private static final Color COLOR_VIEWPORT = Colors.BLUE_LIGHT_5;
    private static final Color  COLOR_WATER = new Color(0x006d7c1d);

    private int                         _mainPosX;
    private int                         _mainPosY;
    private int                         _floor;
    private Sprite                      _spriteMap;
    private View                        _panelMain;
    private int gameWidth;
    private int gameHeight;
    private boolean                     _dirty;
    private Pixmap                      _pixmap;

    @Inject
    private PlantModule plantModule;

    @Inject
    private WorldModule worldModule;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private ItemModule itemModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private UIManager uiManager;

    @Inject
    private Viewport viewport;

    @Inject
    private MainPanelController mainPanelController;

    @Inject
    private GameManager gameManager;

    private int miniMapWidth;
    private int miniMapHeight;
    private float ratioX;
    private float ratioY;

    @Override
    public void onGameStart(Game game) {
        gameWidth = game.getInfo().worldWidth;
        gameHeight = game.getInfo().worldHeight;

        miniMapWidth = (int) (mainPanelController.getMapContainer().getWidth() * applicationConfig.uiScale);
        miniMapHeight = (int) (mainPanelController.getMapContainer().getHeight() * applicationConfig.uiScale);

        ratioX = ((float)miniMapWidth / gameWidth);
        ratioY = ((float)miniMapHeight / gameHeight);

        _pixmap = new Pixmap(gameWidth, gameHeight, Pixmap.Format.RGB888);
        _floor = WorldHelper.getCurrentFloor();
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
        _dirty = true;
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
        _dirty = true;
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (mainPanelController.getRootView().isVisible()) {

            if (_dirty || _spriteMap == null) {
                _dirty = false;
                createMap();
            }

            if (_spriteMap != null) {
                renderer.draw(_spriteMap);
            }

            drawViewport(renderer);
            drawCharacters(renderer);
        }
    }

    private void drawViewport(GDXRenderer renderer) {
        int x = _mainPosX + (int)((Math.min(gameWidth -38-1, Math.max(0, -viewport.getPosX() / 32))) * ratioX);
        int y = _mainPosY + (int)((Math.min(gameHeight -32-1, Math.max(0, -viewport.getPosY() / 32))) * ratioY);
        renderer.drawPixel(x, y, (int) (38 * ratioX), 2, COLOR_VIEWPORT);
        renderer.drawPixel(x, y, 2, (int) (32 * ratioY), COLOR_VIEWPORT);
        renderer.drawPixel(x, (int) (y + 32 * ratioY), (int)(38 * ratioX), 2, COLOR_VIEWPORT);
        renderer.drawPixel((int) (x + 38 * ratioX), y, 1, (int)(32 * ratioY) + 2, COLOR_VIEWPORT);
    }

    private void drawCharacters(GDXRenderer renderer) {
        characterModule.getCharacters().stream()
                .filter(character -> character.getParcel().z == WorldHelper.getCurrentFloor())
                .forEach(character -> renderer.drawPixel(
                        (int) (_mainPosX + (character.getParcel().x * ratioX)),
                        (int) (_mainPosY + (character.getParcel().y * ratioY)),
                        3,
                        3,
                        COLOR_CHARACTER));
    }

    private void createMap() {
        if (gameManager.isLoaded()) {
            _mainPosX = mainPanelController.getMapContainer().getFinalX();
            _mainPosY = mainPanelController.getMapContainer().getFinalY();

            float scaleX = (float)miniMapWidth / gameWidth;
            float scaleY = (float)miniMapHeight / gameHeight;
            float scale = Math.min(scaleX, scaleY);

            int displayWidth = (int)(gameWidth * scale);
            int displayHeight = (int)(gameHeight * scale);

            ParcelModel[][][] parcels = worldModule.getParcels();
            for (int x = 0; x < gameWidth; x++) {
                for (int y = 0; y < gameHeight; y++) {
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
                        _pixmap.drawPixel(x, y, 0x000000ff);
                    }
                }
            }

            _spriteMap = new Sprite(new Texture(_pixmap, Pixmap.Format.RGB888, false));
            _spriteMap.setSize(gameWidth, gameHeight);
            _spriteMap.setRegion(0, 0, gameWidth, gameHeight);
            _spriteMap.flip(false, true);
            _spriteMap.setScale(scale, scale);
            _spriteMap.setPosition(
                    mainPanelController.getMapContainer().getFinalX() + miniMapWidth / 2f - displayWidth / 2f,
                    mainPanelController.getMapContainer().getFinalY() + miniMapHeight / 2f - displayHeight / 2f);
            _spriteMap.setOrigin(0, 0);
        }
    }
}