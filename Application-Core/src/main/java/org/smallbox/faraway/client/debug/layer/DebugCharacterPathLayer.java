package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.util.Constant;

@GameObject
@GameLayer(level = LayerLevel.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugCharacterPathLayer extends BaseLayer {
    @Inject private CharacterModule characterModule;

    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        characterModule.getAll().stream()
                .filter(characterModel -> characterModel.getPath() != null)
                .forEach(character -> character.getPath().getGraphPath().forEach(parcel -> {
                    renderer.drawCircleOnMap(parcel, 5, Color.BLACK, true, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE);
                    renderer.drawCircleOnMap(parcel, 4, Color.WHITE, true, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE);
                }));
    }

}
