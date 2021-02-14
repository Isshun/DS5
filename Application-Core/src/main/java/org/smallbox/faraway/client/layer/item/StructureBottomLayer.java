package org.smallbox.faraway.client.layer.item;

import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.structure.StructureModule;

@GameObject
@GameLayer(level = LayerLevel.WORLD_TOP_LAYER_LEVEL, visible = true)
public class StructureBottomLayer extends BaseMapLayer {
    @Inject private StructureModule _structureModule;
    @Inject private SpriteManager spriteManager;
    @Inject private Game game;

    @Override
    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
    }

}