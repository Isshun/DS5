package org.smallbox.faraway.data.loader;

import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;

public class ItemLoader implements IDataLoader {

    private boolean _hasErrors;

    public void load(GameData data, String path, String packageName) {
        Log.debug("load items...");
        loadDirectory(data, path, packageName, new File(path));
        Log.debug("load items: done");
    }

    private void loadDirectory(GameData data, String path, String packageName, File directory) {
        for (File file: directory.listFiles()) {
            if (file.isDirectory()) {
                loadDirectory(data, path, packageName, file);
            }
            if (file.getName().endsWith(".yml")) {
                loadFile(data, packageName, file);
            }
        }
    }

    private void loadFile(GameData data, String packageName, File itemFile) {
        ItemInfo info = null;

        try {
            Log.debug(" - load: " + itemFile.getName());
            InputStream input = new FileInputStream(itemFile);
            Yaml yaml = new Yaml(new Constructor(ItemInfo.class));
            info = (ItemInfo)yaml.load(input);
            input.close();
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
            } else if ("equipment".equals(info.type) || info.equipment != null) {
                info.isEquipment = true;
                info.isConsumable = true;
            } else {
                throw new RuntimeException("unknown item type: " + info.type);
            }

            data.items.add(info);
        }
    }

    @Override
    public void reloadIfNeeded(GameData data) {
    }

    @Override
    public void load(final GameData data) {
        _hasErrors = false;

        data.items = new ArrayList<>();
        data.gatherItems = new ArrayList<>();

        // First pass
        load(data, "data/items/", "base");
        load(data, "data/mods/garden/items/", "garden");

        secondPass(data);
        thirdPass(data);

        if (_hasErrors) {
            throw new RuntimeException("Errors loading items");
        }
    }

    private void error(ItemInfo item, String message) {
        _hasErrors = true;
        Log.error(message + " (" + item.name + ")");
    }

    private void secondPass(GameData data) {
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

                    // Set product items (for self-product item, like rock)
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

    private void thirdPass(GameData data) {
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
}
