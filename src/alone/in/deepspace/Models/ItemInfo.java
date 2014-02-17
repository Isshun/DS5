package alone.in.DeepSpace.Models;


public class ItemInfo {
	public BaseItem.Type	type;
	public String			name;
	public boolean			solid;
	public int				width;
	public int				height;
	public int				matter;
	public int				power;
	public int				zone;

	public ItemInfo(BaseItem.Type structureRoom, String name, boolean solid, int width, int height, int matter, int power, int zone) {
		this.type = structureRoom;
		this.name = name;
		this.solid = solid;
		this.width = width;
		this.height = height;
		this.matter = matter;
		this.power = power;
		this.zone = zone;
	}
}
