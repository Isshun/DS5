package alone.in.deepspace.World;

import alone.in.deepspace.World.BaseItem.Type;

public class ItemInfo {
	public BaseItem.Type	type;
	public String			name;
	public boolean			solid;
	public int				width;
	public int				height;
	public int				matter;
	public int				power;
	public int				zone;
	public int 				light;

	public ItemInfo(BaseItem.Type type, String name, boolean solid, int width, int height, int matter, int power, int zone) {
		this.light = 0;
		this.type = type;
		this.name = name;
		this.solid = solid;
		this.width = width;
		this.height = height;
		this.matter = matter;
		this.power = power;
		this.zone = zone;
		if (type == Type.QUARTER_CHAIR) {
			this.light = 5;
		}
	}
}
