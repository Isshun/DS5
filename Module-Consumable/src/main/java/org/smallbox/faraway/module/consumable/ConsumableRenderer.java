package org.smallbox.faraway.module.consumable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 31/07/2016.
 */
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
            public void onAddConsumable(ParcelModel parcel, ConsumableModel consumable) {
                addTag("+" + consumable.getQuantity(), consumable.getParcel());
            }

            @Override
            public void onUpdateQuantity(ParcelModel parcel, ConsumableModel consumable, int quantityBefore, int quantityAfter) {
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
                        .draw(renderer, viewport.getRealPosX(parcel.x), viewport.getRealPosY(parcel.y));
                frameLeft--;
            }
        });
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        consumableModule.getConsumables().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(consumable -> renderer.drawOnMap(consumable.getParcel(), getItemSprite(consumable)));

        tags.removeIf(draw -> draw.frameLeft < 0);
        tags.forEach(draw -> draw.onTagDraw(renderer, viewport));

//        renderer.draw("Hello", 12, 100, 100, Color.CYAN);
    }

    private Sprite getItemSprite(ConsumableModel consumable) {
        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    public int getLevel() {
        return MainRenderer.CONSUMABLE_RENDERER_LEVEL;
    }
}
