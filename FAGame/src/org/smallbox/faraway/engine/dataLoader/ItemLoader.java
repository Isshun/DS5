package org.smallbox.faraway.engine.dataLoader;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.ItemInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.stream.Collectors;

public class ItemLoader {

    private static boolean _hasErrors;

    public static void load(GameData data, String path, String packageName) {
	    Log.debug("load items...");

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
			    Log.debug(" - load: " + itemFile.getName());
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
			    if ("consumable".equals(info.type)) {
				    info.isConsumable = true;
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
		
	    Log.debug("items loaded: " + i);
	}

	public static void load(final GameData data) {
        _hasErrors = false;

		// First pass
		ItemLoader.load(data, "data/items/", "base");
		ItemLoader.load(data, "data/mods/garden/items/", "garden");

        secondPass(data);

        thirdPass(data);

        if (_hasErrors) {
            throw new RuntimeException("Errors loading items");
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

    private static void thirdPass(GameData data) {
        for (ItemInfo item: data.items) {
            if (!item.isUserItem && !item.isStructure && item.cost > 0) {
                error(item, "Only UserItem and StructureItem can have cost attribute");
            }
            if (item.receipts != null) {
                for (ItemInfo.ItemInfoReceipt receipt: item.receipts) {
                    if (receipt.products != null && receipt.products.size() > 1) {
                        throw new RuntimeException("Receipt cannot produce multiple items");
                    }
                }
            }
        }
    }

    private static void error(ItemInfo item, String message) {
//        throw new RuntimeException(message + " (" + item.name + ")");
        _hasErrors = true;
        Log.error(message + " (" + item.name + ")");
    }

    private static void secondPass(GameData data) {
        for (ItemInfo item: data.items) {
            item.isSleeping = "base.bed".equals(item.name);

            if (item.receipts != null) {
                for (ItemInfo.ItemInfoReceipt receipt: item.receipts) {
                    for (ItemInfo.ItemComponentInfo component: receipt.components) {
                        component.itemInfo = data.getItemInfo(component.item);
                    }
                    for (ItemInfo.ItemProductInfo product: receipt.products) {
                        product.itemInfo = data.getItemInfo(product.item);
                    }
                }
            }

            if (item.actions != null) {
                for (ItemInfo.ItemInfoAction action: item.actions) {

                    // Set product items (for self-product item, like res_rock)
                    if (action.products != null && !action.products.isEmpty()) {
                        for (ItemInfo.ItemProductInfo productInfo: action.products) {
                            productInfo.itemInfo = data.getItemInfo(productInfo.item);
                            productInfo.dropRate = productInfo.dropRate == 0 ? 1 : productInfo.dropRate;
                        }
                    }

                    // Set receipts (for factory items, like cooker)
                    if (action.receipts != null) {
                        for (ItemInfo.ItemInfoReceipt receiptInfo: action.receipts) {
                            if (receiptInfo.products != null) {
                                for (ItemInfo.ItemProductInfo productInfo : receiptInfo.products) {
                                    productInfo.itemInfo = data.getItemInfo(productInfo.item);
                                }
                            }
                            if (receiptInfo.components != null) {
                                for (ItemInfo.ItemComponentInfo componentInfo : receiptInfo.components) {
                                    componentInfo.itemInfo = data.getItemInfo(componentInfo.item);
                                }
                            }
                        }
                    }

                    if (action.dropRate == 0) {
                        action.dropRate = 1;
                    }

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
                            data.gatherItems.add(item);
                            break;

                        case "mine":
                            if (item.actions.size() > 1) {
                                throw new RuntimeException("action type \"mine\" need to be unique");
                            }
//                            data.gatherItems.add(item);
                            break;
                    }
                }
            }
        }
}

}
