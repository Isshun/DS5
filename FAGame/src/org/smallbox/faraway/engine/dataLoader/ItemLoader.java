package org.smallbox.faraway.engine.dataLoader;

import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.ItemInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
			    
//			    info.isStorage = info.storage > 0 || info.actions != null && info.actions.storage > 0;
//			    info.isFood = info.actions != null && info.actions.effects != null && info.actions.effects.food > 0;
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


			if (item.actions != null) {
				for (ItemInfo.ItemInfoAction action: item.actions) {
					switch (action.type) {
                        case "use":
                            if (item.actions.size() > 1) {
                                throw new RuntimeException("action type \"use\" need to be unique");
                            }
                            break;

						case "cook":
                            break;

						case "gather":
                            if (item.actions.size() > 1) {
                                throw new RuntimeException("action type \"gather\" need to be unique");
                            }

                            if (action.products != null && !action.products.isEmpty()) {
                                action.productsItem = new ArrayList<>();
                                action.productsItem.add(data.getItemInfo(action.products.get(0)));
                            }
                            data.gatherItems.add(item);
                            break;

						case "mine":
                            if (item.actions.size() > 1) {
                                throw new RuntimeException("action type \"mine\" need to be unique");
                            }

                            if (action.products != null && !action.products.isEmpty()) {
                                action.productsItem = new ArrayList<>();
                                action.productsItem.add(data.getItemInfo(action.products.get(0)));
                            }
//                            data.gatherItems.add(item);
                            break;
					}

					break;
				}
			}

//			// Init action item
//			if (item.actions != null) {
//				item.actions.duration *= Constant.DURATION_MULTIPLIER;
//				if (item.actions.products != null) {
//					// Items products
//					item.actions.itemsProduce = new ArrayList<>();
//					for (String itemProduceName: item.actions.products) {
//						item.actions.itemsProduce.add(data.getItemInfo(itemProduceName));
//					}
//
//					// Item accepted for craft
//					item.actions.itemAccept = new ArrayList<>();
//					for (ItemInfo itemProduce: item.actions.itemsProduce) {
//						item.actions.itemAccept.addAll(itemProduce.craftedFromItems);
//					}
//					item.isFactory = item.actions.itemAccept.size() > 0;
//				}
//			}
		}
	}

}
