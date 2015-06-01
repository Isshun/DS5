package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.engine.renderer.MainRenderer;

public class ItemFactory {

	public static ItemBase create(WorldManager manager, WorldArea area, ItemInfo info, int matterSupply) {
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
			return createUserItem(manager, area, info, matterSupply);
		}
	}

	private static UserItem createUserItem(WorldManager manager, WorldArea area, ItemInfo info, int matterSupply) {
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

		// Set world areas
		for (int i = 0; i < item.getWidth(); i++) {
			for (int j = 0; j < item.getHeight(); j++) {
				manager.getArea(area.getX() + i, area.getY() + j).setItem(item);
			}
		}

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
		area.setResource(resource);
		
		return resource;
	}

}
