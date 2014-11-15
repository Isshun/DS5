package alone.in.deepspace.model;

import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.util.Log;

public class BridgeItem {
	public static final int	USER_ITEM = 1;
	public static final int STRUCTURE_ITEM = 2;
	public static final int RESOURCE = 3;
	public static final int AREA = 4;
	
	public int 				sprite;
	public int 				type;
	public int 				x;
	public int 				y;

	public BridgeItem(ItemInfo info, int x, int y) {
		if (info.isUserItem) {
			this.type = USER_ITEM;
		} else if (info.isStructure) {
			this.type = STRUCTURE_ITEM;
		} else if (info.isResource) {
			this.type = USER_ITEM;
		} else {
			this.type = AREA;
		}
//		this.type = USER_ITEM;
		Log.info("sprite: " + info.spriteId);
		this.sprite = info.spriteId;
		this.x = x;
		this.y = y;
	}

	public BridgeItem(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
