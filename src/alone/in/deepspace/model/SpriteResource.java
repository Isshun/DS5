package alone.in.deepspace.model;

import alone.in.deepspace.manager.ServiceManager;

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
