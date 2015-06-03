package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.item.*;

public class ItemFactory {

	public static ItemBase create(WorldManager manager, WorldArea area, ItemInfo info, int value) {
		// Base light item
		if ("base.light".equals(info.name)) {
			area.setLightSource(info.light);
			((MainRenderer)MainRenderer.getInstance()).initLight();
			return null;
		}
		// Consumable item
		else if (info.isConsomable) {
			return createConsumable(area, info, value);
		}
		// World resource
		else if (info.isResource) {
			return createResource(area, info, value);
		}
		// Structure item
		else if (info.isStructure) {
			return createStructure(area, info, value);
		}
		// User item
		else {
			return createUserItem(manager, area, info, value);
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

	private static ConsumableItem createConsumable(WorldArea area, ItemInfo info, int quantity) {
		ConsumableItem consumable = new ConsumableItem(info);

		consumable.setQuantity(quantity);
		consumable.setMatterSupply(100);
		area.setConsumable(consumable);

		return consumable;
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
