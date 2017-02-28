package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
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
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL, visible = true)
public class ConsumableRenderer extends BaseRenderer {

    @BindModule
    private ConsumableModule consumableModule;

    private Queue<TagDraw> tags = new ConcurrentLinkedQueue<>();

    private abstract class TagDraw {
        public int frameLeft = 100;
        public abstract void onTagDraw(GDXRenderer renderer, Viewport viewport);
    }

    private int                     _frame;

    @Override
    public void onGameCreate(Game game) {
        consumableModule.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onAddConsumable(ParcelModel parcel, ConsumableItem consumable) {
                addTag("+" + consumable.getQuantity(), consumable.getParcel());
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
                        .setTextColor(org.smallbox.faraway.core.engine.Color.CYAN)
                        .draw(renderer, viewport.getScreenPosX(parcel.x), viewport.getScreenPosY(parcel.y));
                frameLeft--;
            }
        });
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getConsumables().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(consumable -> {
                    renderer.drawOnMap(consumable.getParcel(), getItemSprite(consumable));
                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getQuantity(), 12, Color.BLUE, 0, 0);
                });

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(renderer, viewport));
    }

    private Sprite getItemSprite(ConsumableItem consumable) {
        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }
}
