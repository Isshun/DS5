package org.smallbox.faraway.client.layer.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.renderer.extra.TextStyle;
import org.smallbox.faraway.client.renderer.extra.TextStyleBuilder;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.consumable.Consumable;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.consumable.ConsumableModuleObserver;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.Constant;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.badlogic.gdx.graphics.Color.CYAN;
import static com.badlogic.gdx.graphics.Color.WHITE;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL, visible = true)
public class ConsumableLayer extends BaseMapLayer {
    private final static TextStyle consumableQuantityStyle = TextStyleBuilder.build("sf", 12, WHITE).autoScale(true).shadow(1).get();

    @Inject private SpriteManager spriteManager;
    @Inject private ConsumableModule consumableModule;
    @Inject private MapRenderer mapRenderer;
    @Inject private InputManager inputManager;

    private final Queue<TagDraw> tags = new ConcurrentLinkedQueue<>();

    private abstract class TagDraw {
        public int frameLeft = 100;
        public abstract void onTagdraw(BaseRenderer renderer, Viewport viewport);
    }

    @Override
    public void onGameStart(Game game) {
        consumableModule.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onAddConsumable(Parcel parcel, Consumable consumable) {
                addTag("+" + consumable.getActualQuantity(), consumable.getParcel());
            }

            @Override
            public void onUpdateQuantity(Parcel parcel, Consumable consumable, int quantityBefore, int quantityAfter) {
                addTag("+" + (quantityAfter - quantityBefore), consumable.getParcel());
            }
        });
    }

    private void addTag(String text, Parcel parcel) {
        tags.add(new TagDraw() {
            @Override
            public void onTagdraw(BaseRenderer renderer, Viewport viewport) {
                UILabel.create(null)
                        .setText(text)
                        .setTextSize(12)
                        .setTextColor(CYAN)
                        .draw(renderer, viewport.getScreenPosX(parcel.x), viewport.getScreenPosY(parcel.y));
                frameLeft--;
            }
        });
    }

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        Parcel parcelOver = WorldHelper.getParcel(viewport.getWorldPosX(inputManager.getMouseX()), viewport.getWorldPosY(inputManager.getMouseY()), viewport.getFloor());

        consumableModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .filter(item -> item.getTotalQuantity() > 0)
                .forEach(consumable -> {
                    Parcel parcel = consumable.getParcel();
                    int offsetX = consumable.getGridPosition() == 1 || consumable.getGridPosition() == 3 ? Constant.HALF_TILE_SIZE : 0;
                    int offsetY = consumable.getGridPosition() == 2 || consumable.getGridPosition() == 3 ? Constant.HALF_TILE_SIZE : 0;
                    Sprite sprite = spriteManager.getOrCreateSprite(consumable.getGraphic(), parcelOver == consumable.getParcel());

                    renderer.drawSpriteOnMap(sprite, parcel, offsetX, offsetY);
//                    renderer.drawRectangleOnMap(consumable.getParcel().x, consumable.getParcel().y, 40, 10, new Color(0x75D0D4FF), true, 0, 0);
                    String stringQuantity = consumable.getTotalQuantity() >= 1000 ? consumable.getTotalQuantity() / 1000 + "k" : String.valueOf(consumable.getTotalQuantity());
//                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, stringQuantity, 30, Color.WHITE, 1, 51, true);

//                    renderer.drawTextOnMapUI(parcel.x, parcel.y, stringQuantity, (int) (8 * (4 - gdxRenderer.getZoom())), Color.WHITE, offsetX, offsetY + 50, false, true);
                    renderer.drawTextOnMap(parcel, stringQuantity, consumableQuantityStyle, offsetX, offsetY + 50);

                    //drawSelectionOnMap(renderer, spriteManager, viewport, consumable, parcel.x, parcel.y, Constant.HALF_TILE_SIZE - 12, Constant.HALF_TILE_SIZE - 12, 6, 6);
                });

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
