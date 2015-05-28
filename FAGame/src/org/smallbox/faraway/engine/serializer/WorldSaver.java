package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.serializer.CharacterSerializer.CharacterSave;
import org.smallbox.faraway.engine.serializer.RoomSerializer.RoomSave;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.*;

import java.util.ArrayList;
import java.util.List;

public class WorldSaver {

	public static class WorldSave {
		public List<WorldSaveArea>	areas;
		public List<RoomSave> 		rooms;
		public List<CharacterSave>	characters;
		
		public WorldSave() {
			areas = new ArrayList<WorldSaveArea>();
		}
	}
	
	private static class WorldSaveArea {
		public WorldSaveStructure 	structure;
		public WorldSaveResource 	resource;
		public WorldSaveUserItem 	item;
		public int					lightSource;
		public int 					x;
		public int 					y;
		public int 					z;
	}
	
	private static class WorldSaveBaseItem {
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
		if (area.getItem() == null && area.getRessource() == null && area.getStructure() == null) {
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
		}
		
		WorldResource resource = area.getRessource();
		if (resource != null) {
			areaSave.resource = new WorldSaveResource();
			areaSave.resource.name = resource.getName();
			areaSave.resource.matter = resource.getMatterSupply();
			areaSave.resource.tile = resource.getTile();
			areaSave.resource.value = (int)resource.getValue();
		}
		
		areas.add(areaSave);
	}

	public static void load(WorldManager worldManager, List<WorldSaveArea> areas) {
	    for (WorldSaveArea area: areas) {
	    	if (area != null) {
	    		// Light
	    		if (area.lightSource > 0) {
	    			worldManager.getArea(area.z, area.x, area.y).setLightSource(area.lightSource);
	    		}
	    		
	    		// UserItem
	    		if (area.item != null) {
	    			ItemBase item = worldManager.putItem(area.item.name, area.x, area.y, area.z, area.item.matter);
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
	    			worldManager.putItem(area.structure.name, area.x, area.y, area.z, area.structure.matter);
	    		}

	    		// Resource
	    		if (area.resource != null) {
	    			ItemBase item = worldManager.putItem(area.resource.name, area.x, area.y, area.z, area.resource.matter);
	    			if (item != null) {
		    			((WorldResource)item).setTile(area.resource.tile);
		    			((WorldResource)item).setValue(area.resource.value);
	    			}
	    		}
	    	}
	    }
	}

	public static void save(WorldManager worldManager, List<WorldSaveArea> areas) {
		for (int z = 0; z < 1; z++) {
			for (int x = 0; x < worldManager.getWidth(); x++) {
				for (int y = 0; y < worldManager.getHeight(); y++) {
					WorldArea area = worldManager.getArea(z, x, y);
					saveArea(areas, area);
				}
			}
		}
	}

}
