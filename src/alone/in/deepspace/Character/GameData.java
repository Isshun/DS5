package alone.in.deepspace.Character;

import java.util.List;

import alone.in.deepspace.World.ItemInfo;

public class GameData {

	public List<ItemInfo> items;

	public ItemInfo getItemInfo(String name) {
		for (ItemInfo info: items) {
			if (info.name.equals(name)) {
				return info;
			}
		}

		// TODO
		return ServiceManager.getData().items.get(0);
	}

}