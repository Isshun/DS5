package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@GameLayer(level = LayerManager.WORLD_GROUND_LAYER_LEVEL, visible = true)
public class WorldBasicGroundLayer extends BaseLayer {

    @BindModule
    private WorldModule worldModule;

    private Queue<TagDraw> tags = new ConcurrentLinkedQueue<>();

    private abstract class TagDraw {
        public int frameLeft = 100;
        public abstract void onTagDraw(GDXRenderer renderer, Viewport viewport);
    }

    private int                     _frame;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getParcelList().stream()
                .filter(viewport::hasParcel)
                .forEach(parcel -> renderer.drawOnMap(parcel, getItemSprite(parcel)));

        tags.removeIf(draw -> draw.frameLeft < 0);
        tags.forEach(draw -> draw.onTagDraw(renderer, viewport));
    }

    private Sprite getItemSprite(ParcelModel parcel) {
        if (parcel.getRockInfo() != null) {
            Sprite sprite = ApplicationClient.spriteManager.getSprite(parcel.getRockInfo(), parcel.getRockInfo().graphics.get(0), 0, 0, 255, false);
            sprite.setRegion(0, 0, 32, 32);
            sprite.setRegionWidth(32);
            sprite.setRegionHeight(32);
            sprite.setBounds(0, 0, 32, 32);
            return sprite;
        }

        ItemInfo itemInfo = Application.data.getItemInfo("base.ground.grass");
        Sprite sprite = ApplicationClient.spriteManager.getSprite(itemInfo, itemInfo.graphics.get(0), 0, 0, 255, false);
        sprite.setRegion(0, 0, 32, 32);
        sprite.setRegionWidth(32);
        sprite.setRegionHeight(32);
        sprite.setBounds(0, 0, 32, 32);
        return sprite;
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

}