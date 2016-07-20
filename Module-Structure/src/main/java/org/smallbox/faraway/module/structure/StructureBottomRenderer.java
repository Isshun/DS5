package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

public class StructureBottomRenderer extends BaseRenderer {
    private final StructureModule _structureModule;
    protected SpriteManager _spriteManager;
    protected MapObjectModel    _itemSelected;
    private int                 _floor;
    private int                 _width;
    private int                 _height;

    public StructureBottomRenderer(StructureModule structureModule) {
        _structureModule = structureModule;
    }

    @Override
    protected void onLoad(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _spriteManager = SpriteManager.getInstance();
    }

    @Override
    public int getLevel() {
        return MainRenderer.WORLD_TOP_RENDERER_LEVEL;
    }

    @Override
    protected void onUpdate() {
        _structureModule.getStructures().forEach(structure -> {
            if (structure.isDoor()) {
                boolean isOpen = false;
                // TODO
//                for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {
//                    if (Math.abs(character.getParcel().x - structure.getParcel().x) <= 1 && Math.abs(character.getParcel().y - structure.getParcel().y) <= 1) {
//                        isOpen = true;
//                    }
//                }
                structure.setTile(isOpen ? 1 : 0);
            }
        });
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
    }

    @Override
    public void onRefresh(int frame) {
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