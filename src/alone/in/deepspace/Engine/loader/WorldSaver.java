package alone.in.deepspace.engine.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.WorldManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.UserItem;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.util.Log;

public class WorldSaver {

	private static class WorldSave {
		public List<WorldSaveArea>	areas;
		
		public WorldSave() {
			areas = new ArrayList<WorldSaveArea>();
		}
	}
	
	private static class WorldSaveArea {
		public WorldSaveStructure 	structure;
		public WorldSaveUserItem 	item;
		public int 					x;
		public int 					y;
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
	
	public static void save(WorldManager worldManager, String filePath) {
		WorldSave save = new WorldSave();
		
		for (int x = 0; x < worldManager.getWidth(); x++) {
			for (int y = 0; y < worldManager.getHeight(); y++) {
				WorldArea area = worldManager.getArea(x, y);
				saveArea(save, area);
			}
		}
		
		try {
			OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(filePath));
		    Yaml yaml = new Yaml();
		    StringWriter writer = new StringWriter();
		    yaml.dump(save, output);
		    System.out.println(writer.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void saveArea(WorldSave save, WorldArea area) {
		WorldSaveArea areaSave = new WorldSaveArea();
		
		areaSave.x = area.getX();
		areaSave.y = area.getY();
		
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
				for (BaseItem storredItem: storage.getInventory()) {
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
		
		save.areas.add(areaSave);
	}

	public static void load(WorldManager worldManager, String filePath) {
	    Log.info("load world");
	    long time = System.currentTimeMillis();
		
		try {
			InputStream input = new FileInputStream(filePath);
		    Yaml yaml = new Yaml(new Constructor(WorldSave.class));
		    WorldSave worldSave = (WorldSave)yaml.load(input);
		    
		    for (WorldSaveArea area: worldSave.areas) {
		    	if (area != null) {
		    		// UserItem
		    		if (area.item != null) {
		    			BaseItem item = worldManager.putItem(area.item.name, area.x, area.y, area.item.matter);
		    			if (area.item.storage != null) {
		    				StorageItem storage = ((StorageItem)item);
		    				storage.setStorageFilter(area.item.storage.acceptFood,
		    						area.item.storage.acceptDrink,
		    						area.item.storage.acceptConsomable,
		    						area.item.storage.acceptGarbage);
		    				for (String storredItemName: area.item.storage.inventory) {
		    					storage.addInventory(new UserItem(ServiceManager.getData().getItemInfo(storredItemName)));
		    				}
		    			}
		    		}
		    		// Structure
		    		if (area.structure != null) {
		    			worldManager.putItem(area.structure.name, area.x, area.y, area.structure.matter);
		    		}
		    	}
		    }

		    Log.info("load complete: " + (System.currentTimeMillis() - time) + "ms");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
