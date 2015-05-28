package org.smallbox.faraway.engine.dataLoader;

import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.ItemInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;

public class ItemLoader {
	
	public static void load(GameData data, String path, String packageName) {
	    System.out.println("load items...");

	    // List files
		File itemFiles[] = (new File(path)).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.contains(".yml");
			}
		});
		
		// Load files
		int i = 0;
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

			    // Get category
			    if ("consomable".equals(info.type)) {
				    info.isConsomable = true;
			    } else if ("structure".equals(info.type)) {
				    info.isStructure = true; 
			    } else if ("item".equals(info.type)) {
				    info.isUserItem = true; 
			    } else if ("resource".equals(info.type)) {
				    info.isResource = true; 
			    } else {
			    	throw new RuntimeException("unknow item type: " + info.type);
			    }
			    
//			    info.isStorage = info.storage > 0 || info.onAction != null && info.onAction.storage > 0;
			    info.isFood = info.onAction != null && info.onAction.effects != null && info.onAction.effects.food > 0;
			    data.items.add(info);
			}
			
			i++;
		}
		
	    System.out.println("items loaded: " + i);
	}

	public static void load(final GameData data) {
		ItemLoader.load(data, "data/items/", "base");
		ItemLoader.load(data, "data/mods/garden/items/", "garden");
		
		// First pass
		for (ItemInfo item: data.items) {
			// Init crafted item
			if (item.craftedFrom != null) {
				item.craftedFromItems = new ArrayList<ItemInfo>();
				for (String name: item.craftedFrom) {
					item.craftedFromItems.add(data.getItemInfo(name));
				}
			}
		}

		// Second pass
		for (ItemInfo item: data.items) {
			item.isSleeping = "base.bed".equals(item.name);
			
			// Init gather item
			if (item.onGather != null) {
				item.onGather.itemProduce = data.getItemInfo(item.onGather.produce);
				data.gatherItems.add(item);
			}
			
			// Init mine item
			if (item.onMine != null) {
				item.onMine.itemProduce = data.getItemInfo(item.onMine.produce);
			}
			
			// Init action item
			if (item.onAction != null) {
				item.onAction.duration *= Constant.DURATION_MULTIPLIER;
				if (item.onAction.produce != null) {
					// Items produce
					item.onAction.itemsProduce = new ArrayList<ItemInfo>();
					for (String itemProduceName: item.onAction.produce) {
						item.onAction.itemsProduce.add(data.getItemInfo(itemProduceName));
					}
					
					// Item accepted for craft
					item.onAction.itemAccept = new ArrayList<ItemInfo>();
					for (ItemInfo itemProduce: item.onAction.itemsProduce) {
						item.onAction.itemAccept.addAll(itemProduce.craftedFromItems);
					}
					item.isFactory = item.onAction.itemAccept.size() > 0;
				}
			}
		}
	}

}
