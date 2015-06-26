package org.smallbox.faraway.engine;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.item.StructureModel;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class SpriteManager {
    private static SpriteManager    _manager;

    public static SpriteManager     getInstance() {
        return _manager;
    }
    public static void              setInstance(SpriteManager manager) {
        _manager = manager;
    }

	public abstract SpriteModel     getExterior(int index, int floor);
    public abstract SpriteModel     getFloor(StructureModel item, int zone, int room);
	public abstract SpriteModel     getSimpleWall(int zone);
	public abstract SpriteModel     getWall(StructureModel item, int special, int index, int zone);
	public abstract SpriteModel     getCharacter(CharacterModel c, int dirIndex, int frame);
    public abstract SpriteModel     getItem(MapObjectModel item, int tile);
    public abstract SpriteModel     getItem(MapObjectModel item);
    public abstract SpriteModel     getFoe(Object object, int direction, int frame);
	public abstract SpriteModel     getBullet(int i);
    public abstract SpriteModel     getIcon(ItemInfo info);
	public abstract SpriteModel     getIconChecked();
	public abstract SpriteModel     getIconUnChecked();
    public abstract SpriteModel     getGreenHouse(int index);
	public abstract SpriteModel     getResource(ResourceModel resource);
    public abstract SpriteModel     getNoOxygen();
	public abstract SpriteModel     getSelector(int tile);
	public abstract SpriteModel     getSelectorCorner(int corner);
	public abstract SpriteModel     getSelector(MapObjectModel item, int frame);
    public abstract SpriteModel     getIcon(String path);

    public abstract RenderEffect    createRenderEffect();
    public abstract Viewport        createViewport();

    public abstract SpriteModel     getGround(int type);

    public abstract SpriteModel getAnimal(String path);
}
