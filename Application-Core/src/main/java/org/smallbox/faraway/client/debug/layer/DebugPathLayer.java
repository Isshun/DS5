package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugPathLayer extends BaseMapLayer {
    private static final Color GREEN = new Color(0x00ff00dd);
    private static final Color RED = new Color(0xff0000ff);
    @Inject private WorldModule worldModule;
    @Inject private CharacterModule characterModule;
    @Inject private PathManager pathManager;

    public void    onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getAll().stream()
                .filter(viewport::hasParcel)
                .forEach(parcel -> {
                    // Border
                    renderer.drawCadreOnMap(parcel.x, parcel.y, Constant.TILE_SIZE - 1, Constant.TILE_SIZE - 1, parcel.isWalkable() ? GREEN : RED, 4, 0, 0);

                    Color color;

                    // Top
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 1, 6, color, Constant.HALF_TILE_SIZE, 0);

                    // Bottom
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 1, 6, color, Constant.HALF_TILE_SIZE, 26);

                    // Left
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 6, 1, color, 0, Constant.HALF_TILE_SIZE);

                    // Right
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel.x, parcel.y, 6, 1, color, 26, Constant.HALF_TILE_SIZE);
                });

        characterModule.getAll().stream()
                .filter(characterModel -> characterModel.getPath() != null)
                .forEach(character -> character.getPath().getNodes().forEach(parcel -> {
                    renderer.drawCircleOnMap(parcel.x, parcel.y, 5, Color.BLACK, true, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE);
                    renderer.drawCircleOnMap(parcel.x, parcel.y, 4, Color.WHITE, true, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE);
                }));
    }

}
