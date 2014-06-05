package alone.in.deepspace.engine.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.engine.loader.CharacterSerializer.CharacterSave;
import alone.in.deepspace.manager.RoomSave;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.WorldManager;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.StorageItem;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.util.Log;

import com.thoughtworks.xstream.XStream;

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
		public int 					x;
		public int 					y;
		public int 					z;
	}
	
	private static class WorldSaveBaseItem {
		public String 				name;
		public int 					matter;
	}
	
	private static class WorldSaveUserItem extends WorldSaveBaseItem {
		public WorldSaveStorageInfo	storage;
	}
	
	private static class WorldSaveStorageInfo extends WorldSaveBaseItem {
		public List<String> 		inventory;
		public boolean 				acceptFood;
		public boolean 				acceptDrink;
		public boolean 				acceptConsomable;
		public boolean 				acceptGarbage;
		
		public WorldSaveStorageInfo() {
			inventory = new ArrayList<String>();
		}
	}
	
	private static class WorldSaveStructure extends WorldSaveBaseItem {
	}

	private static class WorldSaveResource extends WorldSaveBaseItem {
		public int tile;
		public int value;
	}
	
	public static void save(WorldManager worldManager, String filePath) {
		System.gc();
		
		WorldSave save = new WorldSave();
		
		(new RoomSerializer()).save(save);
		(new CharacterSerializer()).save(save);

		for (int z = 0; z < 1; z++) {
			for (int x = 0; x < worldManager.getWidth(); x++) {
				for (int y = 0; y < worldManager.getHeight(); y++) {
					WorldArea area = worldManager.getArea(z, x, y);
					saveArea(save, area);
				}
			}
		}

		XStream xstream = new XStream();
		String xml = xstream.toXML(save);
		
		try {
			FileOutputStream fs = new FileOutputStream(filePath + ".xml");
			OutputStreamWriter output = new OutputStreamWriter(fs);
//		    StringWriter writer = new StringWriter();
//		    writer.write(xml);
			output.write(xml);
			output.close();
		    fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		try {
//			FileOutputStream fs = new FileOutputStream(filePath);
//			OutputStreamWriter output = new OutputStreamWriter(fs);
//		    Yaml yaml = new Yaml();
//		    StringWriter writer = new StringWriter();
//		    yaml.dump(save, output);
//		    fs.close();
//		    System.out.println(writer.toString());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private static void saveArea(WorldSave save, WorldArea area) {
		if (area.getItem() == null && area.getRessource() == null && area.getStructure() == null) {
			return;
		}
		WorldSaveArea areaSave = new WorldSaveArea();
		
		areaSave.x = area.getX();
		areaSave.y = area.getY();
		areaSave.z = area.getZ();
		
		UserItem item = area.getItem();
		if (item != null) {
			areaSave.item = new WorldSaveUserItem();
			areaSave.item.name = item.getName();
			areaSave.item.matter = item.getMatterSupply();
			if (item.isStorage()) {
				StorageItem storage = (StorageItem)item;
				areaSave.item.storage = new WorldSaveStorageInfo();
				areaSave.item.storage.acceptFood = storage.acceptFood();
				areaSave.item.storage.acceptDrink = storage.acceptDrink();
				areaSave.item.storage.acceptConsomable = storage.acceptConsomable();
				areaSave.item.storage.acceptGarbage = storage.acceptGarbage();
				for (ItemBase storredItem: storage.getInventory()) {
					areaSave.item.storage.inventory.add(storredItem.getName());
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
		
		save.areas.add(areaSave);
	}

	public static void load(WorldManager worldManager, String filePath) {
		System.gc();

		Log.info("load world");
	    long time = System.currentTimeMillis();
	    WorldSave worldSave = null;

		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        System.out.print("" + used);
	    
        // 12 - 215mb + 140% / 1700ms + 70%
        // 12 - 88mb / 1000ms
		try {
			InputStream input = new FileInputStream(filePath + ".xml");
			XStream xstream = new XStream();
			worldSave = (WorldSave)xstream.fromXML(input);
		    input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (worldSave != null) {
			if (worldSave.rooms != null) {
				(new RoomSerializer()).load(worldSave);
			}

			if (worldSave.characters != null) {
				(new CharacterSerializer()).load(worldSave);
			}
			
		    for (WorldSaveArea area: worldSave.areas) {
		    	if (area != null) {
		    		// UserItem
		    		if (area.item != null) {
		    			ItemBase item = worldManager.putItem(area.item.name, area.x, area.y, area.z, area.item.matter);
		    			if (area.item.storage != null) {
		    				StorageItem storage = ((StorageItem)item);
		    				storage.setStorageFilter(area.item.storage.acceptFood,
		    						area.item.storage.acceptDrink,
		    						area.item.storage.acceptConsomable,
		    						area.item.storage.acceptGarbage);
		    				for (String storredItemName: area.item.storage.inventory) {
		    					ItemInfo info = ServiceManager.getData().getItemInfo(storredItemName);
		    					storage.addInventory(new UserItem(info));
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
	    
	    Log.info("load complete: " + (System.currentTimeMillis() - time) + "ms");
        used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        System.out.print("" + used);
        
		System.gc();
	}

}
