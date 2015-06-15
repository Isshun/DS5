package org.smallbox.faraway.manager;

import org.smallbox.faraway.model.item.*;

public class ItemFactory {

	public static MapObjectModel create(WorldManager manager, ItemInfo info, int value) {
		return create(manager, null, info, value);
	}

	public static MapObjectModel create(WorldManager manager, ParcelModel area, ItemInfo info, int value) {
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
			return createStructure(area, info, value == -1);
		}
		// User item
		else {
			return createUserItem(manager, area, info, value == -1);
		}
	}

	public static ItemModel createUserItem(WorldManager manager, ParcelModel area, ItemInfo info, boolean isComplete) {
		ItemModel item = new ItemModel(info);
		item.addProgress(isComplete ? info.cost : 0);

		// Set world areas
		for (int i = 0; i < item.getWidth(); i++) {
			for (int j = 0; j < item.getHeight(); j++) {
				manager.getParcel(area.getX() + i, area.getY() + j).setItem(item);
			}
		}

		return item;
	}

	public static ConsumableModel createConsumable(ParcelModel area, ItemInfo info, int quantity) {
		ConsumableModel consumable = new ConsumableModel(info);

		consumable.setQuantity(quantity);

		if (area != null) {
			area.setConsumable(consumable);
		}

		return consumable;
	}

	public static StructureModel createStructure(ParcelModel area, ItemInfo info, boolean isComplete) {
		StructureModel structure = new StructureModel(info);

		structure.addProgress(isComplete ? info.cost : 0);
		area.setStructure(structure);

		return structure;
	}

	public static MapObjectModel createResource(ParcelModel area, ItemInfo info, int matterSupply) {
		ResourceModel resource = new ResourceModel(info);

		resource.setValue(matterSupply);
		area.setResource(resource);

		return resource;
	}
}
