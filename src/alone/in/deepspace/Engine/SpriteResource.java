package alone.in.deepspace.Engine;

import alone.in.deepspace.World.ItemInfo;

public class SpriteResource {

	// TODO
	public ItemInfo			info;
	public int				posX;
	public int				posY;
	public int				textureIndex;
	
	public SpriteResource(ItemInfo info, int x, int y, int texture) {
		this.info = info;
		this.posX = x;
		this.posY = y;
		this.textureIndex = texture;
	}

}
