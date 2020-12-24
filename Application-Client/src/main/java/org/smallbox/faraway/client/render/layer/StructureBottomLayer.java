package org.smallbox.faraway.client.render.layer;

import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.modules.structure.StructureModule;

@GameObject
@GameLayer(level = LayerManager.WORLD_TOP_LAYER_LEVEL, visible = true)
public class StructureBottomLayer extends BaseLayer {

    @Inject
    private StructureModule _structureModule;

    @Inject
    private SpriteManager spriteManager;

    protected MapObjectModel    _itemSelected;
    private int                 _floor;
    private int                 _width;
    private int                 _height;

    @Override
    public void onGameStart(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}