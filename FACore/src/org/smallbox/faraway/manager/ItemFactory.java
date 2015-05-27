package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.renderer.MainRenderer;
import org.smallbox.faraway.model.item.FactoryItem;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.StackItem;
import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldArea;
import org.smallbox.faraway.model.item.WorldResource;
import org.smallbox.faraway.engine.util.Log;

public class ItemFactory {

	public static ItemBase create(WorldArea area, ItemInfo info, int matterSupply) {
		// Base light item
		if ("base.light".equals(info.name)) {
			area.setLightSource(info.light);
			((MainRenderer)MainRenderer.getInstance()).initLight();
			return null;
		}
		// Storage item
		else if (info.isStorage) {
			Log.error("storage item is deprecated: " + info.name);
			return null;
		}
		// World resource
		else if (info.isResource) {
			return createResource(area, info, matterSupply);
		}
		// Structure item
		else if (info.isStructure) {
			return createStructure(area, info, matterSupply);
		}
		// User item
		else {
			return createUserItem(area, info, matterSupply);
		}
	}

	private static UserItem createUserItem(WorldArea area, ItemInfo info, int matterSupply) {
		UserItem item = null;

		// Factory item
		if (info.isFactory) {
			FactoryItem factory = new FactoryItem(info);
			Game.getWorldManager().addFactory(factory);
			item = factory;
		}
		// Stack item
		else if (info.isStack) {
			item = new StackItem();
		}
		// Regular user item
		else {
			item = new UserItem(info);
		}
		
		item.setMatterSupply(matterSupply);
		area.setItem(item);
		
		return item;
	}

	private static StructureItem createStructure(WorldArea area, ItemInfo info, int matterSupply) {
		StructureItem structure = new StructureItem(info);
		
		structure.setMatterSupply(matterSupply);
		area.setStructure(structure);

		return structure;
	}

	private static ItemBase createResource(WorldArea area, ItemInfo info, int matterSupply) {
		WorldResource resource = new WorldResource(info);
		
		resource.setValue(matterSupply);
		area.setRessource(resource);
		
		return resource;
	}

}
