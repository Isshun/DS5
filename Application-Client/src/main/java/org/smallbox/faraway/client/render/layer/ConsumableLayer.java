package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumableModuleObserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 31/07/2016.
 */
@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL, visible = true)
public class ConsumableLayer extends BaseLayer {

    @BindComponent
    private SpriteManager spriteManager;

    @BindComponent
    private ConsumableModule consumableModule;

    private Queue<TagDraw> tags = new ConcurrentLinkedQueue<>();

    private abstract class TagDraw {
        public int frameLeft = 100;
        public abstract void onTagDraw(GDXRenderer renderer, Viewport viewport);
    }

    @Override
    public void onGameStart(Game game) {
        consumableModule.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onAddConsumable(ParcelModel parcel, ConsumableItem consumable) {
                addTag("+" + consumable.getFreeQuantity(), consumable.getParcel());
            }

            @Override
            public void onUpdateQuantity(ParcelModel parcel, ConsumableItem consumable, int quantityBefore, int quantityAfter) {
                addTag("+" + (quantityAfter - quantityBefore), consumable.getParcel());
            }
        });
    }

    private void addTag(String text, ParcelModel parcel) {
        tags.add(new TagDraw() {
            @Override
            public void onTagDraw(GDXRenderer renderer, Viewport viewport) {
                UILabel.create(null)
                        .setText(text)
                        .setTextSize(12)
                        .setTextColor(Color.CYAN)
                        .draw(renderer, viewport.getScreenPosX(parcel.x), viewport.getScreenPosY(parcel.y));
                frameLeft--;
            }
        });
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getConsumables().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(consumable -> {
                    renderer.drawOnMap(consumable.getParcel(), ApplicationClient.spriteManager.getNewSprite(consumable.getGraphic()));
//                    renderer.drawRectangleOnMap(consumable.getParcel().x, consumable.getParcel().y, 40, 10, new Color(0x75D0D4FF), true, 0, 0);
                    String stringQuantity = consumable.getTotalQuantity() >= 1000 ? consumable.getTotalQuantity() / 1000 + "k" : String.valueOf(consumable.getTotalQuantity());
                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, stringQuantity, 12, new Color(0x000000FF), 16, 16);
                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, stringQuantity, 12, new Color(0x9de6e7FF), 15, 15);

                    drawSelectionOnMap(renderer, spriteManager, viewport, consumable, consumable.getParcel().x, consumable.getParcel().y, 20, 20, 6, 6);
                });

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
