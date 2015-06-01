package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.*;

import java.util.List;

public class WorldSerializer implements SerializerInterface {

    public static class WorldSaveArea {
		public WorldSaveStructure 	structure;
		public WorldSaveResource 	resource;
		public WorldSaveUserItem 	item;
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
		public WorldSaveStackInfo	stack;
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
	
	private static void saveArea(List<WorldSaveArea> areas, WorldArea area) {
		if (area.getItem() == null && area.getResource() == null && area.getStructure() == null) {
			return;
		}
		WorldSaveArea areaSave = new WorldSaveArea();
		
		areaSave.x = area.getX();
		areaSave.y = area.getY();
		areaSave.z = area.getZ();
		areaSave.lightSource = area.getLightSource();
		
		UserItem item = area.getItem();
		if (item != null) {
			areaSave.item = new WorldSaveUserItem();
			areaSave.item.id = item.getId();
			areaSave.item.name = item.getName();
			areaSave.item.matter = item.getMatterSupply();
			if (item.isStack()) {
				StackItem stack = (StackItem)item;
				if (stack.getStackedInfo() != null) {
					areaSave.item.stack = new WorldSaveStackInfo();
					areaSave.item.stack.item = stack.getStackedInfo().name;
					areaSave.item.stack.count = stack.size();
				}
			}
		}
		
		StructureItem structure = area.getStructure();
		if (structure != null) {
			areaSave.structure = new WorldSaveStructure();
			areaSave.structure.name = structure.getName();
			areaSave.structure.matter = structure.getMatterSupply();
			areaSave.structure.id = structure.getId();
		}
		
		WorldResource resource = area.getResource();
		if (resource != null) {
			areaSave.resource = new WorldSaveResource();
			areaSave.resource.name = resource.getName();
			areaSave.resource.matter = resource.getMatterSupply();
			areaSave.resource.tile = resource.getTile();
			areaSave.resource.value = (int)resource.getValue();
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
                    WorldArea area = manager.getArea(z, x, y);
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
                    ItemBase item = manager.putItem(area.item.name, area.x, area.y, area.z, area.item.matter);
                    if (item != null) {
                        item.setId(area.item.id);
                    }
                    if (area.item.stack != null) {
                        StackItem stack = ((StackItem)item);
                        ItemInfo info = Game.getData().getItemInfo(area.item.stack.item);
                        for (int i = 0; i < area.item.stack.count; i++) {
                            stack.add(new UserItem(info));
                        }
                    }
                }

                // Structure
                if (area.structure != null) {
                    ItemBase item = manager.putItem(area.structure.name, area.x, area.y, area.z, area.structure.matter);
                    item.setId(area.structure.id);
                }

                // Resource
                if (area.resource != null) {
                    ItemBase item = manager.putItem(area.resource.name, area.x, area.y, area.z, area.resource.matter);
                    if (item != null) {
                        ((WorldResource)item).setTile(area.resource.tile);
                        ((WorldResource)item).setValue(area.resource.value);
                        item.setId(area.resource.id);
                    }
                }
            }
        }
    }

}
