package alone.in.deepspace.model;

import alone.in.deepspace.Game;
import alone.in.deepspace.model.item.ItemInfo;

public class SpriteResource {

	// TODO
	public ItemInfo			info;
	public int				posX;
	public int				posY;
	public int				textureIndex;
	
	public SpriteResource(String name, int x, int y, int texture) {
		this.info = Game.getData().getItemInfo(name);
		this.posX = x;
		this.posY = y;
		this.textureIndex = texture;
	}

}
