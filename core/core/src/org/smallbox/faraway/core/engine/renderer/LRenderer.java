package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.RenderLayer;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.SpriteModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryReceiptModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

public class LRenderer extends BaseRenderer {
    private static Color[] COLORS = new Color[] {
            new Color(0, 0, 0, .9f),
            new Color(0, 0, 0, .8f),
            new Color(0, 0, 0, .7f),
            new Color(0, 0, 0, .6f),
            new Color(0, 0, 0, .5f),
            new Color(0, 0, 0, .4f),
            new Color(0, 0, 0, .3f),
            new Color(0, 0, 0, .2f),
            new Color(0, 0, 0, .1f),
            new Color(0, 0, 0, .0f),
            new Color(0, 0, 0, .0f),
    };

    @Override
    public void init() {
    }

    public int getLevel() {
        return -99;
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    private void refreshLayerLight(RenderLayer layer, int fromX, int fromY, int toX, int toY) {
        Log.info("Refresh layer: " + layer.getIndex());

//        int mb = 1024 * 1024;
//        Runtime runtime = Runtime.getRuntime();
//        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
//        int total = (int) (runtime.totalMemory() / mb);
//        System.out.println("RefreshLayer: " + used + "/" + total);

        layer.begin();
        layer.setRefresh();
        for (int x = toX - 1; x >= fromX; x--) {
            for (int y = toY - 1; y >= fromY; y--) {
                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                if (parcel != null) {
//                    layer.draw(colors[Math.min(10, (int)(parcel.getLight() * 10))], (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                }
            }
        }
        layer.end();
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = (int) ((-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        int fromY = (int) ((-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        int toX = fromX + 50;
        int toY = fromY + 40;

        int offsetX = viewport.getPosX();
        int offsetY = viewport.getPosY();

        for (int x = toX; x >= fromX; x--) {
            for (int y = toY; y >= fromY; y--) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);
                if (parcel != null) {
                    renderer.draw(COLORS[((int)(parcel.getLight() * 10))], (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
                    //renderer.draw(_spriteManager.getGround(1), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
                }
            }
        }
    }

    @Override
    public void onRefresh(int frame) {
    }
}
