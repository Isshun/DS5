package org.smallbox.faraway.manager;

import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.Viewport;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.model.item.WorldResource;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class SpriteManager {
    private static SpriteManager _manager;

    public static SpriteManager getInstance() {
        return _manager;
    }

    public static void setInstance(SpriteManager manager) {
        _manager = manager;
    }

	public abstract SpriteModel getExterior(int index, int floor);
    public abstract SpriteModel getFloor(StructureItem item, int zone, int room);
	public abstract SpriteModel getSimpleWall(int zone);
	public abstract SpriteModel getWall(StructureItem item, int special, int index, int zone);
	public abstract SpriteModel getCharacter(CharacterModel c, int dirIndex, int frame);
	public abstract SpriteModel getCharacter(ProfessionModel profession, int direction, int frame, int extra);
    public abstract SpriteModel getItem(ItemBase item, int tile);
    public abstract SpriteModel getItem(ItemBase item);
    public abstract SpriteModel getFoe(Object object, int direction, int frame);
	public abstract SpriteModel getBullet(int i);
    public abstract SpriteModel getIcon(ItemInfo info);
	public abstract SpriteModel getIconChecked();
	public abstract SpriteModel getIconUnChecked();
    public abstract SpriteModel getGreenHouse(int index);
	public abstract SpriteModel getResource(WorldResource resource);
    public abstract SpriteModel getNoOxygen();
	public abstract SpriteModel getSelector(int tile);
	public abstract SpriteModel getSelectorCorner(int corner);
	public abstract SpriteModel getSelector(ItemBase item, int frame);
    public abstract RenderEffect createRenderEffect();
    public abstract Viewport createViewport();
    public abstract SpriteModel getIcon(String path);
}
