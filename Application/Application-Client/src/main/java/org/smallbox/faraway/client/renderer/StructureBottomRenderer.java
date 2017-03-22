package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.modules.structure.StructureModule;

@GameRenderer(level = MainRenderer.WORLD_TOP_RENDERER_LEVEL, visible = true)
public class StructureBottomRenderer extends BaseRenderer {
    @BindModule
    private StructureModule _structureModule;

    protected SpriteManager _spriteManager;
    protected MapObjectModel    _itemSelected;
    private int                 _floor;
    private int                 _width;
    private int                 _height;

    @Override
    public void onGameStart(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _spriteManager = ApplicationClient.spriteManager;
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