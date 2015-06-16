package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.*;

import java.util.ArrayList;
import java.util.List;

public class WorldSerializer implements SerializerInterface {

	public static class WorldSaveParcel {
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
		public int 					progress;
		public int 					health;
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

	private static void saveParcel(List<WorldSaveParcel> parcels, ParcelModel parcel) {
//		if (parcel.getItem() == null && parcel.getResource() == null && parcel.getStructure() == null) {
//			return;
//		}
		WorldSaveParcel parcelSave = new WorldSaveParcel();

		parcelSave.x = parcel.getX();
		parcelSave.y = parcel.getY();
		parcelSave.z = parcel.getZ();
		parcelSave.lightSource = parcel.getLightSource();

		ItemModel item = parcel.getRootItem();
		if (item != null) {
			parcelSave.item = new WorldSaveUserItem();
			parcelSave.item.id = item.getId();
			parcelSave.item.name = item.getName();
			parcelSave.item.progress = item.getProgress();
		}

        ConsumableModel consumable = parcel.getConsumable();
		if (consumable != null) {
			parcelSave.consumable = new WorldSaveConsumableItem();
			parcelSave.consumable.id = consumable.getId();
			parcelSave.consumable.name = consumable.getName();
			parcelSave.consumable.quantity = consumable.getQuantity();
		}

		StructureModel structure = parcel.getStructure();
		if (structure != null) {
			parcelSave.structure = new WorldSaveStructure();
			parcelSave.structure.name = structure.getName();
			parcelSave.structure.progress = structure.getProgress();
			parcelSave.structure.id = structure.getId();
			parcelSave.structure.health = structure.getHealth();
		}

		ResourceModel resource = parcel.getResource();
		if (resource != null) {
			parcelSave.resource = new WorldSaveResource();
			parcelSave.resource.name = resource.getName();
			parcelSave.resource.tile = resource.getTile();
			parcelSave.resource.value = resource.getQuantity();
			parcelSave.resource.id = resource.getId();
		}

		parcels.add(parcelSave);
	}

	@Override
	public void save(GameSerializer.GameSave save) {
		WorldManager manager = ServiceManager.getWorldMap();

		save.parcels = new ArrayList<>();
		for (int z = 0; z < 1; z++) {
			for (int x = 0; x < manager.getWidth(); x++) {
				for (int y = 0; y < manager.getHeight(); y++) {
					ParcelModel parcel = manager.getParcel(z, x, y);
					saveParcel(save.parcels, parcel);
				}
			}
		}
	}

	@Override
	public void load(GameSerializer.GameSave save) {
		WorldManager manager = ServiceManager.getWorldMap();

		for (WorldSaveParcel parcel: save.parcels) {
			if (parcel != null) {
				manager.getParcel(parcel.z, parcel.x, parcel.y).setBlood(Math.max(0, Math.random() * 10 - 5));
				manager.getParcel(parcel.z, parcel.x, parcel.y).setSnow(Math.max(0, Math.random() * 10 - 5));
				manager.getParcel(parcel.z, parcel.x, parcel.y).setDirt(Math.max(0, Math.random() * 10 - 5));
				manager.getParcel(parcel.z, parcel.x, parcel.y).setRubble(Math.max(0, Math.random() * 10 - 5));

				// UserItem
				if (parcel.item != null) {
					ItemModel item = (ItemModel)manager.putObject(parcel.item.name, parcel.x, parcel.y, parcel.z, parcel.item.progress);
					if (item != null) {
						item.setId(parcel.item.id);
						item.setHealth(parcel.item.health);
					}
				}

				// Consumable
				if (parcel.consumable != null) {
					ConsumableModel consumable = (ConsumableModel)manager.putObject(parcel.consumable.name, parcel.x, parcel.y, parcel.z, parcel.consumable.quantity);
					if (consumable != null) {
						consumable.setId(parcel.consumable.id);
                        consumable.setQuantity(parcel.consumable.quantity);
					}
				}

				// Structure
				if (parcel.structure != null) {
					StructureModel structure = (StructureModel)manager.putObject(parcel.structure.name, parcel.x, parcel.y, parcel.z, parcel.structure.progress);
					structure.setId(parcel.structure.id);
					structure.setHealth(parcel.structure.health);
				}

				// Resource
				if (parcel.resource != null) {
					MapObjectModel item = manager.putObject(parcel.resource.name, parcel.x, parcel.y, parcel.z, parcel.resource.progress);
					if (item != null) {
						((ResourceModel)item).setTile(parcel.resource.tile);
						((ResourceModel)item).setValue(parcel.resource.value);
						item.setId(parcel.resource.id);
					}
				}
			}
		}

//		WorldFactory.addMountain(manager);
//
//		for (int x = 0; x < manager.getWidth(); x++) {
//			for (int y = 0; y < manager.getHeight(); y++) {
//				if (Math.random() > 0.995 && manager.getParcel(x, y).isEmpty()) {
//					manager.getParcel(x, y).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//				}
//			}
//		}
//
//		for (int i = 0; i < 5; i++) {
//			for (int x = 0; x < manager.getWidth(); x++) {
//				for (int y = 0; y < manager.getHeight(); y++) {
//					if (manager.getParcel(x, y).getResource() != null && manager.getParcel(x, y).getResource().getInfo().name.equals("base.res_rock")) {
//						if (Math.random() > 0.5 && x < manager.getWidth() && manager.getParcel(x + 1, y) != null && manager.getParcel(x + 1, y).isEmpty())
//							manager.getParcel(x + 1, y).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//						if (Math.random() > 0.5 && x > 0 && manager.getParcel(x - 1, y) != null && manager.getParcel(x - 1, y).isEmpty())
//							manager.getParcel(x - 1, y).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//						if (Math.random() > 0.5 && y < manager.getHeight() && manager.getParcel(x, y + 1) != null && manager.getParcel(x, y + 1).isEmpty())
//							manager.getParcel(x, y + 1).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//						if (Math.random() > 0.5 && y > 0 && manager.getParcel(x, y - 1) != null && manager.getParcel(x, y - 1).isEmpty())
//							manager.getParcel(x, y - 1).setResource(new WorldResource(Game.getData().getItemInfo("base.res_rock")));
//					}
//				}
//			}
//		}
//
//		for (int x = 0; x < manager.getWidth(); x++) {
//			for (int y = 0; y < manager.getHeight(); y++) {
//				if (manager.getParcel(x, y) != null && manager.getParcel(x, y).getResource() != null) {
//					manager.getParcel(x, y).getResource().setMatterSupply(100);
//				}
//			}
//		}
	}

}
