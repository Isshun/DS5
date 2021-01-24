package org.smallbox.faraway.client.layer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.controller.SystemInfoController;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.client.ui.extra.Colors;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.structure.StructureItem;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerManager.MINI_MAP_LEVEL, visible = true)
public class MinimapLayer extends BaseLayer {
    //    private static final int    COLOR_background = 0xfffff9bdff;
    private static final int COLOR_ROCK = 0x60442dff;
    private static final int COLOR_ROCK_GROUND = 0x80644dff;
    private static final int COLOR_PLANT = 0x9bcd4dff;
    private static final int COLOR_STRUCTURE = 0x333333ff;
    private static final int COLOR_ITEM = 0xff3333ff;
    private static final Color COLOR_CHARACTER = new Color(0x3c59ffff);
    private static final Color COLOR_VIEWPORT = Colors.BLUE_LIGHT_3;
    private static final int COLOR_WATER = 0x006d7c1d;

    @Inject private PlantModule plantModule;
    @Inject private WorldModule worldModule;
    @Inject private CharacterModule characterModule;
    @Inject private ItemModule itemModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private Viewport viewport;
    @Inject private SystemInfoController mainPanelController;
    @Inject private GameManager gameManager;
    @Inject private AssetManager assetManager;
    @Inject private Game game;

    private List<MinimapRule> rules = Arrays.asList(
            new MinimapRule(parcel -> parcel.hasItem(StructureItem.class), parcel -> COLOR_STRUCTURE),
            new MinimapRule(parcel -> plantModule.getPlant(parcel) != null, parcel -> COLOR_PLANT),
            new MinimapRule(Parcel::hasRock, parcel -> COLOR_ROCK),
            new MinimapRule(Parcel::hasGround, parcel -> parcel.getGroundInfo().color),
            new MinimapRule(Parcel::hasLiquid, parcel -> parcel.getLiquidInfo().color),
            new MinimapRule(parcel -> true, parcel -> 0xff0000ff)
    );

    private int _mainPosX;
    private int _mainPosY;
    private int _floor;
    private Sprite _spriteMap;
    private View _panelMain;
    private int gameWidth;
    private int gameHeight;
    private boolean _dirty;
    private Pixmap _pixmap;
    private Texture _pixmapTexture;
    private int miniMapWidth;
    private int miniMapHeight;
    private float ratioX;
    private float ratioY;

    public Sprite getSprite() {
        return _spriteMap;
    }

    private class MinimapRule {
        private final Predicate<Parcel> predicate;
        private final Function<Parcel, Integer> function;

        public MinimapRule(Predicate<Parcel> predicate, Function<Parcel, Integer> function) {
            this.predicate = predicate;
            this.function = function;
        }

        public boolean matches(Parcel parcel) {
            return predicate.test(parcel);
        }

        public int getColor(Parcel parcel) {
            return function.apply(parcel);
        }
    }

    @AfterGameLayerInit
    public void layerInit() {
        if (mainPanelController != null) {
            gameWidth = game.getInfo().worldWidth;
            gameHeight = game.getInfo().worldHeight;

            miniMapWidth = (int) (mainPanelController.getMapContainer().getWidth() * applicationConfig.uiScale);
            miniMapHeight = (int) (mainPanelController.getMapContainer().getHeight() * applicationConfig.uiScale);

            ratioX = ((float) miniMapWidth / gameWidth);
            ratioY = ((float) miniMapHeight / gameHeight);
            _pixmap = assetManager.createPixmap(gameWidth, gameHeight, Pixmap.Format.RGB888);
            _floor = WorldHelper.getCurrentFloor();
            _dirty = true;
        }
    }

    @Override
    public void onGameLongUpdate(Game game) {
        if (mainPanelController != null) {
            Optional.ofNullable(mainPanelController.getMapContainer()).ifPresent(container -> {
                if (miniMapWidth != (int) (container.getWidth() * applicationConfig.uiScale) || miniMapHeight != (int) (container.getHeight() * applicationConfig.uiScale)) {
                    layerInit();
                }
            });
        }
    }

    @Override
    public void onGameStop(Game game) {
        _pixmap.dispose();
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
        _dirty = true;
    }

    @Override
    public void onRemoveRock(Parcel parcel) {
        _dirty = true;
    }

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (mainPanelController != null && mainPanelController.getRootView().isVisible()) {

            if (_dirty || _spriteMap == null) {
                createMap();
            }

//            if (_spriteMap != null) {
//                renderer.drawUI(_spriteMap);
//            }

            drawViewport(renderer);
            drawCharacters(renderer);
            drawUI(renderer);
        }
    }

    private void drawUI(BaseRenderer renderer) {
        mainPanelController.getMapContainer().getViews().stream().filter(view -> !StringUtils.equals(view.getId(), "minimap"))
                .forEach(view -> view.draw(renderer, view.getGeometry().getFinalX(), view.getGeometry().getFinalY()));
    }

    private void drawViewport(BaseRenderer renderer) {
        int x = _mainPosX + (int) ((Math.min(gameWidth - 38 - 1, Math.max(0, -viewport.getPosX() / TILE_SIZE))) * ratioX);
        int y = _mainPosY + (int) ((Math.min(gameHeight - 32 - 1, Math.max(0, -viewport.getPosY() / TILE_SIZE))) * ratioY);
        int rectWidth = (int) (38 * ratioX);
        int rectHeight = (int) (32 * ratioY);
        renderer.drawRectangle(x, y, rectWidth, 2, COLOR_VIEWPORT);
        renderer.drawRectangle(x, y, 2, rectHeight, COLOR_VIEWPORT);
        renderer.drawRectangle(x, y + rectHeight, rectWidth, 2, COLOR_VIEWPORT);
        renderer.drawRectangle(x + rectWidth, y, 2, rectHeight + 2, COLOR_VIEWPORT);
    }

    private void drawCharacters(BaseRenderer renderer) {
        characterModule.getAll().stream()
                .filter(character -> character.getParcel().z == WorldHelper.getCurrentFloor())
                .forEach(character -> renderer.drawRectangle(
                        (int) (_mainPosX + (character.getParcel().x * ratioX)),
                        (int) (_mainPosY + (character.getParcel().y * ratioY)),
                        3,
                        3,
                        COLOR_CHARACTER));
    }

    private void createMap() {
        if (_pixmap != null) {
            _dirty = false;
            _mainPosX = mainPanelController.getMapContainer().getGeometry().getFinalX();
            _mainPosY = mainPanelController.getMapContainer().getGeometry().getFinalY();

            float scaleX = (float) miniMapWidth / gameWidth;
            float scaleY = (float) miniMapHeight / gameHeight;
            float scale = Math.min(scaleX, scaleY);

            int displayWidth = (int) (gameWidth * scale);
            int displayHeight = (int) (gameHeight * scale);

            for (int x = 0; x < gameWidth; x++) {
                for (int y = 0; y < gameHeight; y++) {
                    Optional.ofNullable(worldModule.getParcel(x, y, _floor))
                            .ifPresent(parcel -> rules.stream()
                                    .filter(rule -> rule.matches(parcel))
                                    .findFirst()
                                    .ifPresent(rule -> _pixmap.drawPixel(parcel.x, parcel.y, rule.getColor(parcel))));
                }
            }

            if (_pixmapTexture != null) {
                _pixmapTexture.dispose();
            }

            _pixmapTexture = new Texture(_pixmap, Pixmap.Format.RGB888, false);
            _spriteMap = new Sprite(_pixmapTexture);
            _spriteMap.setSize(gameWidth, gameHeight);
            _spriteMap.setRegion(0, 0, gameWidth, gameHeight);
            _spriteMap.flip(false, true);
            _spriteMap.setScale(scale, scale);
            _spriteMap.setPosition(
                    mainPanelController.getMapContainer().getGeometry().getFinalX(),
                    mainPanelController.getMapContainer().getGeometry().getFinalY()
            );
//            _spriteMap.setPosition(
//                    mainPanelController.getMapContainer().getGeometry().getFinalX() + miniMapWidth / 2f - displayWidth / 2f,
//                    mainPanelController.getMapContainer().getGeometry().getFinalY() + miniMapHeight / 2f - displayHeight / 2f);
            _spriteMap.setOrigin(0, 0);
        }
    }
}