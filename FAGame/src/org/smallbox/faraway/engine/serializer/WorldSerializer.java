package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.*;

import java.util.List;

public class WorldSerializer implements SerializerInterface {

	public static class WorldSaveArea {
		public WorldSaveStructure 	structure;
		public WorldSaveResource 	resource;
		public WorldSaveUserItem 	item;
		public WorldSaveConsumableItem consumable;
		public int					lightSource;
		public int 					x;
		public int 					y;
		public int 					z;
	}

	private static class WorldSaveBaseItem {
		public int 					id;
		public String 				name;
		public int 					matter;
	}

	private static class WorldSaveUserItem extends WorldSaveBaseItem {
//		public WorldSaveStackInfo	stack;
	}

	private static class WorldSaveConsumableItem extends WorldSaveBaseItem {
        public int 					quantity;
	}

	private static class WorldSaveStackInfo {
		public String				item;
		public int					count;
	}

	private static class WorldSaveStructure extends WorldSaveBaseItem {
	}

	private static class WorldSaveResource extends WorldSaveBaseItem {
		public int tile;
		public int value;
	}

	private static void saveArea(List<WorldSaveArea> areas, AreaModel area) {
		if (area.getItem() == null && area.getResource() == null && area.getStructure() == null) {
			return;
		}
		WorldSaveArea areaSave = new WorldSaveArea();

		areaSave.x = area.getX();
		areaSave.y = area.getY();
		areaSave.z = area.getZ();
		areaSave.lightSource = area.getLightSource();

		ItemModel item = area.getRootItem();
		if (item != null) {
			areaSave.item = new WorldSaveUserItem();
			areaSave.item.id = item.getId();
			areaSave.item.name = item.getName();
			areaSave.item.matter = (int)item.getMatterSupply();
		}

        ConsumableModel consumable = area.getConsumable();
		if (consumable != null) {
			areaSave.consumable = new WorldSaveConsumableItem();
			areaSave.consumable.id = consumable.getId();
			areaSave.consumable.name = consumable.getName();
			areaSave.consumable.quantity = consumable.getQuantity();
		}

		StructureModel structure = area.getStructure();
		if (structure != null) {
			areaSave.structure = new WorldSaveStructure();
			areaSave.structure.name = structure.getName();
			areaSave.structure.matter = (int)structure.getMatterSupply();
			areaSave.structure.id = structure.getId();
		}

		ResourceModel resource = area.getResource();
		if (resource != null) {
			areaSave.resource = new WorldSaveResource();
			areaSave.resource.name = resource.getName();
			areaSave.resource.matter = (int)resource.getMatterSupply();
			areaSave.resource.tile = resource.getTile();
			areaSave.resource.value = (int)resource.getQuantity();
			areaSave.resource.id = resource.getId();
		}

		areas.add(areaSave);
	}

	@Override
	public void save(GameSerializer.GameSave save) {
		WorldManager manager = ServiceManager.getWorldMap();

		for (int z = 0; z < 1; z++) {
			for (int x = 0; x < manager.getWidth(); x++) {
				for (int y = 0; y < manager.getHeight(); y++) {
					AreaModel area = manager.getArea(z, x, y);
					saveArea(save.areas, area);
				}
			}
		}
	}

	@Override
	public void load(GameSerializer.GameSave save) {
		WorldManager manager = ServiceManager.getWorldMap();

		for (WorldSaveArea area: save.areas) {
			if (area != null) {
				// Light
				if (area.lightSource > 0) {
					manager.getArea(area.z, area.x, area.y).setLightSource(area.lightSource);
				}

				// UserItem
				if (area.item != null) {
					ItemModel item = (ItemModel) manager.putObject(area.item.name, area.x, area.y, area.z, area.item.matter);
					if (item != null) {
						item.setId(area.item.id);
					}
				}

				// Consumable
				if (area.consumable != null) {
					ConsumableModel consumable = (ConsumableModel) manager.putObject(area.consumable.name, area.x, area.y, area.z, area.consumable.quantity);
					if (consumable != null) {
						consumable.setId(area.consumable.id);
                        consumable.setQuantity(area.consumable.quantity);
					}
				}

				// Structure
				if (area.structure != null) {
					MapObjectModel item = manager.putObject(area.structure.name, area.x, area.y, area.z, area.structure.matter);
					item.setId(area.structure.id);
				}

				// Resource
				if (area.resource != null) {
					MapObjectModel item = manager.putObject(area.resource.name, area.x, area.y, area.z, area.resource.matter);
					if (item != null) {
						((ResourceModel)item).setTile(area.resource.tile);
						((ResourceModel)item).setValue(area.resource.value);
						item.setId(area.resource.id);
					}
				}
			}
		}
//
//		for (int x = 0; x < manager.getWidth(); x++) {
//			for (int y = 0; y < manager.getHeight(); y++) {
//				if (Math.random() > 0.995 && manager.getArea(x, y).isEmpty()) {
//					manager.getArea(x, y).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//				}
//			}
//		}
//
//		for (int i = 0; i < 5; i++) {
//			for (int x = 0; x < manager.getWidth(); x++) {
//				for (int y = 0; y < manager.getHeight(); y++) {
//					if (manager.getArea(x, y).getResource() != null && manager.getArea(x, y).getResource().getInfo().name.equals("base.res_rock")) {
//						if (Math.random() > 0.5 && x < manager.getWidth() && manager.getArea(x + 1, y) != null && manager.getArea(x + 1, y).isEmpty())
//							manager.getArea(x + 1, y).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//						if (Math.random() > 0.5 && x > 0 && manager.getArea(x - 1, y) != null && manager.getArea(x - 1, y).isEmpty())
//							manager.getArea(x - 1, y).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//						if (Math.random() > 0.5 && y < manager.getHeight() && manager.getArea(x, y + 1) != null && manager.getArea(x, y + 1).isEmpty())
//							manager.getArea(x, y + 1).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//						if (Math.random() > 0.5 && y > 0 && manager.getArea(x, y - 1) != null && manager.getArea(x, y - 1).isEmpty())
//							manager.getArea(x, y - 1).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//					}
//				}
//			}
//		}
//
//		for (int x = 0; x < manager.getWidth(); x++) {
//			for (int y = 0; y < manager.getHeight(); y++) {
//				if (manager.getArea(x, y) != null && manager.getArea(x, y).getResource() != null) {
//					manager.getArea(x, y).getResource().setMatterSupply(100);
//				}
//			}
//		}
	}

}
