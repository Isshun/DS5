package org.smallbox.faraway.model;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.model.item.ItemInfo;

public class SpriteResource {

	// TODO
	public ItemInfo			info;
	public int				posX;
	public int				posY;
	public int				textureIndex;
	
	public SpriteResource(String name, int x, int y, int texture) {
		this.info = GameData.getData().getItemInfo(name);
		this.posX = x;
		this.posY = y;
		this.textureIndex = texture;
	}

}
