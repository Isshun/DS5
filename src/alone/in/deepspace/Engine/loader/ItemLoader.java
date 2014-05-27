package alone.in.deepspace.engine.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.util.Constant;

public class ItemLoader {
	
	public static void load(String path, String packageName) {
	    System.out.println("load items...");

	    List<ItemInfo> items = new ArrayList<ItemInfo>();
	    
	    // List files
		File itemFiles[] = (new File(path)).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.contains(".yml");
			}
		});
		
		// Load files
		for (File itemFile: itemFiles) {
			ItemInfo info = null;
			
			try {
			    System.out.println(" - load: " + itemFile.getName());
			    InputStream input = new FileInputStream(itemFile);
			    Yaml yaml = new Yaml(new Constructor(ItemInfo.class));
			    info = (ItemInfo)yaml.load(input);
			    input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (info != null) {
			    info.fileName = itemFile.getName().substring(0, itemFile.getName().length() - 4);
			    info.packageName = packageName;
			    info.name = info.packageName +  '.' + info.fileName;
			    info.isWalkable = true;

			    // Get category
			    if ("consomable".equals(info.category)) {
				    info.isConsomable = true;
			    } else if ("structure".equals(info.category)) {
				    info.isStructure = true; 
			    } else if ("item".equals(info.category)) {
				    info.isUserItem = true; 
			    } else if ("resource".equals(info.category)) {
				    info.isResource = true; 
			    } else {
			    	throw new RuntimeException("unknow item category: " + info.category);
			    }
			    
			    info.isStorage = info.storage > 0 || info.onAction != null && info.onAction.storage > 0;
			    info.isFood = info.onAction != null && info.onAction.effects != null && info.onAction.effects.food > 0;
			    items.add(info);
			}
		}
		
	    System.out.println("items loaded: " + items.size());
	    
	    ServiceManager.getData().items.addAll(items);
	}

	public static void load() {
		ItemLoader.load("data/items/", "base");
		ItemLoader.load("mods/garden/items/", "garden");
		for (ItemInfo item: ServiceManager.getData().items) {
			if (item.craftedFrom != null) {
				item.craftedFromItems = new ArrayList<ItemInfo>();
				for (String name: item.craftedFrom) {
					item.craftedFromItems.add(ServiceManager.getData().getItemInfo(name));
				}
			}
		}
			
		for (ItemInfo item: ServiceManager.getData().items) {
			if (item.onGather != null) {
				item.onGather.itemProduce = ServiceManager.getData().getItemInfo(item.onGather.produce);
			}
			if (item.onMine != null) {
				item.onMine.itemProduce = ServiceManager.getData().getItemInfo(item.onMine.produce);
			}
			if (item.onAction != null) {
				item.onAction.duration *= Constant.DURATION_MULTIPLIER;
				if (item.onAction.produce != null) {
					// Items produce
					item.onAction.itemsProduce = new ArrayList<ItemInfo>();
					for (String itemProduceName: item.onAction.produce) {
						item.onAction.itemsProduce.add(ServiceManager.getData().getItemInfo(itemProduceName));
					}
					
					// Item accepted for craft
					item.onAction.itemAccept = new ArrayList<ItemInfo>();
					for (ItemInfo itemProduce: item.onAction.itemsProduce) {
						item.onAction.itemAccept.addAll(itemProduce.craftedFromItems);
					}
					item.isDispenser = item.onAction.itemAccept.size() > 0;
				}
			}
		}
	}

}
