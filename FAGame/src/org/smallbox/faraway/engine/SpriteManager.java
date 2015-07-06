package org.smallbox.faraway.engine;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ResourceModel;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class SpriteManager {
    private static SpriteManager    _manager;

    public static void              setInstance(SpriteManager manager) { _manager = manager; }

    public static SpriteManager     getInstance() { return _manager; }
	public abstract SpriteModel     getCharacter(CharacterModel c, int dirIndex, int frame);
    public abstract SpriteModel     getItem(MapObjectModel item, int tile);
    public abstract SpriteModel     getItem(MapObjectModel item);
    public abstract SpriteModel     getIcon(ItemInfo info);
	public abstract SpriteModel     getResource(ResourceModel resource);
	public abstract SpriteModel     getSelector(int tile);
	public abstract SpriteModel     getSelectorCorner(int corner);
    public abstract SpriteModel     getIcon(String path);
    public abstract SpriteModel     getGround(int type);
    public abstract SpriteModel     getAnimal(String path);

    public abstract RenderEffect    createRenderEffect();
    public abstract Viewport        createViewport();
}
