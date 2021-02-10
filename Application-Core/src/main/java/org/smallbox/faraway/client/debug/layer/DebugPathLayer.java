package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Constant;

@GameObject
@GameLayer(level = LayerLevel.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugPathLayer extends BaseMapLayer {
    private static final Color GREEN = new Color(0x00ff00dd);
    private static final Color RED = new Color(0xff0000ff);

    @Inject private WorldModule worldModule;
    @Inject private CharacterModule characterModule;
    @Inject private PathManager pathManager;

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getAll().stream()
                .filter(viewport::hasParcel)
                .forEach(parcel -> {
                    // Border
                    renderer.drawCadreOnMap(parcel, Constant.TILE_SIZE - 1, Constant.TILE_SIZE - 1, parcel.isWalkable() ? GREEN : RED, 4, 0, 0);

                    Color color;

                    // Top
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel, 4, 24, color, Constant.HALF_TILE_SIZE, 0);

                    // Bottom
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel, 4, 24, color, Constant.HALF_TILE_SIZE, Constant.TILE_SIZE - 24);

                    // Left
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel, 24, 4, color, 0, Constant.HALF_TILE_SIZE);

                    // Right
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel, 24, 4, color, Constant.TILE_SIZE - 24, Constant.HALF_TILE_SIZE);

                    // Above
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x, parcel.y, parcel.z + 1)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel, 12, 12, color, Constant.HALF_TILE_SIZE - 6, Constant.HALF_TILE_SIZE - 12);

                    // Below
                    color = pathManager.hasConnection(parcel, WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1)) ? GREEN : RED;
                    renderer.drawRectangleOnMap(parcel, 12, 12, color, Constant.HALF_TILE_SIZE - 6, Constant.HALF_TILE_SIZE + 12);
                });

        characterModule.getAll().stream()
                .filter(characterModel -> characterModel.getPath() != null)
                .forEach(character -> character.getPath().getGraphPath().forEach(parcel -> {
                    renderer.drawCircleOnMap(parcel, 5, Color.BLACK, true, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE);
                    renderer.drawCircleOnMap(parcel, 4, Color.WHITE, true, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE);
                }));
    }

}
