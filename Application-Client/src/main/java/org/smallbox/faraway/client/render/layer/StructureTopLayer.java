package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.structure.StructureModule;

@GameObject
@GameLayer(level = LayerManager.STRUCTURE_LAYER_LEVEL, visible = true)
public class StructureTopLayer extends BaseLayer {

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private StructureModule structureModule;

    protected MapObjectModel    _itemSelected;

//    @Override
//    protected void onRenderUpdate() {
//        structureModule.getStructures().forEach(structure -> {
//            ParcelModel parcel = structure.getParcel();
//
//            if ((structure.isWall() || structure.isDoor()) && structure.getInfo().graphics != null) {
//                int tile = 0;
//                if (WorldHelper.hasRock(parcel.x - 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y - 1, parcel.z)) { tile |= 0b10000000; }
//                if (WorldHelper.hasRock(parcel.x,     parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y - 1, parcel.z)) { tile |= 0b01000000; }
//                if (WorldHelper.hasRock(parcel.x + 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y - 1, parcel.z)) { tile |= 0b00100000; }
//                if (WorldHelper.hasRock(parcel.x - 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y,     parcel.z)) { tile |= 0b00010000; }
//                if (WorldHelper.hasRock(parcel.x + 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y,     parcel.z)) { tile |= 0b00001000; }
//                if (WorldHelper.hasRock(parcel.x - 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y + 1, parcel.z)) { tile |= 0b00000100; }
//                if (WorldHelper.hasRock(parcel.x,     parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y + 1, parcel.z)) { tile |= 0b00000010; }
//                if (WorldHelper.hasRock(parcel.x + 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y + 1, parcel.z)) { tile |= 0b00000001; }
//                parcel.setTile(tile);
//            }
//        });
//    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        structureModule.getStructures().stream()
                .filter(structure -> viewport.hasParcel(structure.getParcel()))
                .forEach(structure -> {

                    renderer.drawOnMap(structure.getParcel(), getSprite(structure));

                    if (structure.getHealth() < structure.getMaxHealth()) {
                        renderer.drawTextOnMap(structure.getParcel(), structure.getHealth() + "/" + structure.getMaxHealth(), 14, Color.CHARTREUSE, 0, 0);
                    }

//                    if (!structure.isComplete()) {
//                        renderer.drawTextOnMap(structure.getParcel(), "to build", 14, Color.CHARTREUSE, 0, 0);
//                    }

                });

    }

    private Sprite getSprite(StructureItem structure) {
        return spriteManager.getSprite(structure.getInfo(), structure.getGraphic(), structure.isComplete() ? structure.getInfo().height : 0, 0, 255, false);
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}