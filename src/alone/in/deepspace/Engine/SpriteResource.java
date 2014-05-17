package alone.in.deepspace.Engine;

import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.World.ItemInfo;

public class SpriteResource {

	// TODO
	public ItemInfo			info;
	public int				posX;
	public int				posY;
	public int				textureIndex;
	
	public SpriteResource(String name, int x, int y, int texture) {
		this.info = ServiceManager.getData().getItemInfo(name);
		this.posX = x;
		this.posY = y;
		this.textureIndex = texture;
	}

}
