package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.structure.StructureModule;

@GameRenderer(level = MainRenderer.STRUCTURE_RENDERER_LEVEL, visible = true)
public class StructureTopRenderer extends BaseRenderer {

    @BindComponent
    private SpriteManager spriteManager;

    @BindModule
    private StructureModule structureModule;

    protected MapObjectModel    _itemSelected;

    @Override
    protected void onGameUpdate() {
        structureModule.getStructures().forEach(structure -> {
            ParcelModel parcel = structure.getParcel();

            if ((structure.isWall() || structure.isDoor()) && structure.getInfo().graphics != null) {
                int tile = 0;
                if (WorldHelper.hasRock(parcel.x - 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y - 1, parcel.z)) { tile |= 0b10000000; }
                if (WorldHelper.hasRock(parcel.x,     parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y - 1, parcel.z)) { tile |= 0b01000000; }
                if (WorldHelper.hasRock(parcel.x + 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y - 1, parcel.z)) { tile |= 0b00100000; }
                if (WorldHelper.hasRock(parcel.x - 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y,     parcel.z)) { tile |= 0b00010000; }
                if (WorldHelper.hasRock(parcel.x + 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y,     parcel.z)) { tile |= 0b00001000; }
                if (WorldHelper.hasRock(parcel.x - 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y + 1, parcel.z)) { tile |= 0b00000100; }
                if (WorldHelper.hasRock(parcel.x,     parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y + 1, parcel.z)) { tile |= 0b00000010; }
                if (WorldHelper.hasRock(parcel.x + 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y + 1, parcel.z)) { tile |= 0b00000001; }
                parcel.setTile(tile);
            }
        });
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        Sprite buildSprite = spriteManager.getIcon("../Module-Structure/src/main/resources/ic_build.png", 32, 32);
        structureModule.getStructures().stream()
                .filter(structure -> viewport.hasParcel(structure.getParcel()))
                .forEach(structure -> renderer.drawOnMap(structure.getParcel(), structure.isComplete() ? getSprite(structure) : buildSprite));
    }

    private Sprite getSprite(StructureItem structure) {
        return spriteManager.getSprite(structure.getInfo(), structure.getGraphic(), structure.isComplete() ? structure.getInfo().height : 0, 0, 255, false);
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}