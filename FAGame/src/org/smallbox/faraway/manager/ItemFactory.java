package org.smallbox.faraway.manager;

import org.smallbox.faraway.model.item.*;

public class ItemFactory {

	public static MapObjectModel create(WorldManager manager, ItemInfo info, int value) {
		return create(manager, null, info, value);
	}

	public static MapObjectModel create(WorldManager manager, AreaModel area, ItemInfo info, int value) {
//		// Base light item
//		if ("base.light".equals(info.name)) {
//			area.setLightSource(info.light);
//			((MainRenderer)MainRenderer.getInstance()).initLight();
//			return null;
//		}
		// Consumable item
		if (info.isConsumable) {
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

	private static ItemModel createUserItem(WorldManager manager, AreaModel area, ItemInfo info, int matterSupply) {
		ItemModel item = new ItemModel(info);
		item.setMatterSupply(matterSupply);

		// Set world areas
		for (int i = 0; i < item.getWidth(); i++) {
			for (int j = 0; j < item.getHeight(); j++) {
				manager.getArea(area.getX() + i, area.getY() + j).setItem(item);
			}
		}

		return item;
	}

	private static ConsumableModel createConsumable(AreaModel area, ItemInfo info, int quantity) {
		ConsumableModel consumable = new ConsumableModel(info);

		consumable.setQuantity(quantity);
		consumable.setMatterSupply(100);

		if (area != null) {
			area.setConsumable(consumable);
		}

		return consumable;
	}

	private static StructureModel createStructure(AreaModel area, ItemInfo info, int matterSupply) {
		StructureModel structure = new StructureModel(info);

		structure.setMatterSupply(matterSupply);
		area.setStructure(structure);

		return structure;
	}

	private static MapObjectModel createResource(AreaModel area, ItemInfo info, int matterSupply) {
		ResourceModel resource = new ResourceModel(info);

		resource.setValue(matterSupply);
		area.setResource(resource);

		return resource;
	}
}
