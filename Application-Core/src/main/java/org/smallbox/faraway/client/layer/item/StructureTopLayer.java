package org.smallbox.faraway.client.layer.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.structure.StructureItem;
import org.smallbox.faraway.game.structure.StructureModule;

@GameObject
@GameLayer(level = LayerManager.STRUCTURE_LAYER_LEVEL, visible = true)
public class StructureTopLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private StructureModule structureModule;

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
    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        structureModule.getAll().stream()
                .filter(structure -> viewport.hasParcel(structure.getParcel()))
                .forEach(structure -> {

                    renderer.drawSpriteOnMap(getSprite(structure), structure.getParcel());

                    if (structure.getHealth() < structure.getMaxHealth()) {
                        renderer.drawTextOnMap(structure.getParcel(), structure.getHealth() + "/" + structure.getMaxHealth(), Color.CHARTREUSE, 14, 0, 0);
                    }

//                    if (!structure.isComplete()) {
//                        renderer.drawTextOnMap(structure.getParcel(), "to build", 14, Color.CHARTREUSE, 0, 0);
//                    }

                });

    }

    private Sprite getSprite(StructureItem structure) {
        return spriteManager.getOrCreateSprite(structure.getGraphic());
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}