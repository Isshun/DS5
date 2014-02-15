package alone.in.deepspace;
public class SpriteResource {

	public BaseItem.Type	type;
	public int				posX;
	public int				posY;
	public int				textureIndex;

	
	public SpriteResource(BaseItem.Type type, int x, int y, int texture) {
		this.type = type;
		this.posX = x;
		this.posY = y;
		this.textureIndex = texture;
	}

}
