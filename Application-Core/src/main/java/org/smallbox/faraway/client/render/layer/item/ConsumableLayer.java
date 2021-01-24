package org.smallbox.faraway.client.render.layer.item;

import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.*;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumableModuleObserver;
import org.smallbox.faraway.util.Constant;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.badlogic.gdx.graphics.Color.CYAN;
import static com.badlogic.gdx.graphics.Color.WHITE;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL, visible = true)
public class ConsumableLayer extends BaseMapLayer {
    private final static TextStyle consumableQuantityStyle = TextStyleBuilder.build("sui", 12, WHITE).autoScale(true).shadow(2).get();

    @Inject private SpriteManager spriteManager;
    @Inject private ConsumableModule consumableModule;
    @Inject private GDXRenderer gdxRenderer;

    private final Queue<TagDraw> tags = new ConcurrentLinkedQueue<>();

    private abstract class TagDraw {
        public int frameLeft = 100;
        public abstract void onTagdraw(GDXRendererBase renderer, Viewport viewport);
    }

    @Override
    public void onGameStart(Game game) {
        consumableModule.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onAddConsumable(Parcel parcel, ConsumableItem consumable) {
                addTag("+" + consumable.getFreeQuantity(), consumable.getParcel());
            }

            @Override
            public void onUpdateQuantity(Parcel parcel, ConsumableItem consumable, int quantityBefore, int quantityAfter) {
                addTag("+" + (quantityAfter - quantityBefore), consumable.getParcel());
            }
        });
    }

    private void addTag(String text, Parcel parcel) {
        tags.add(new TagDraw() {
            @Override
            public void onTagdraw(GDXRendererBase renderer, Viewport viewport) {
                UILabel.create(null)
                        .setText(text)
                        .setTextSize(12)
                        .setTextColor(CYAN)
                        .draw(renderer, viewport.getScreenPosX(parcel.x), viewport.getScreenPosY(parcel.y));
                frameLeft--;
            }
        });
    }

    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .filter(item -> item.getTotalQuantity() > 0)
                .forEach(consumable -> {
                    Parcel parcel = consumable.getParcel();
                    int offsetX = consumable.getStack() == 1 || consumable.getStack() == 3 ? Constant.HALF_TILE_SIZE : 0;
                    int offsetY = consumable.getStack() == 2 || consumable.getStack() == 3 ? Constant.HALF_TILE_SIZE : 0;

                    renderer.drawSpriteOnMap(spriteManager.getNewSprite(consumable.getGraphic()), parcel, offsetX, offsetY);
//                    renderer.drawRectangleOnMap(consumable.getParcel().x, consumable.getParcel().y, 40, 10, new Color(0x75D0D4FF), true, 0, 0);
                    String stringQuantity = consumable.getTotalQuantity() >= 1000 ? consumable.getTotalQuantity() / 1000 + "k" : String.valueOf(consumable.getTotalQuantity());
//                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, stringQuantity, 30, Color.WHITE, 1, 51, true);

//                    renderer.drawTextOnMapUI(parcel.x, parcel.y, stringQuantity, (int) (8 * (4 - gdxRenderer.getZoom())), Color.WHITE, offsetX, offsetY + 50, false, true);
                    renderer.drawTextOnMap(parcel, stringQuantity, consumableQuantityStyle, offsetX, offsetX + 50);

                    drawSelectionOnMap(renderer, spriteManager, viewport, consumable, parcel.x, parcel.y, 20, 20, 6, 6);
                });

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
