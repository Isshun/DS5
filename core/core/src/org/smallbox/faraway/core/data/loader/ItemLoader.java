package org.smallbox.faraway.core.data.loader;

import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.util.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ItemLoader implements IDataLoader {

    private boolean _hasErrors;

    public void load(GameData data, String path, String packageName) {
        Log.debug("load items...");
//        FileUtils.listRecursively(path).stream().filter(file -> file.getName().endsWith(".yml")).forEach(file ->
//                loadFile(data, packageName, file));
        Log.debug("load items: done");
    }

    private void loadFile(GameData data, String packageName, File itemFile) {
        ItemInfo info = null;

        try {
            Log.info(" - load: " + itemFile.getName());
            InputStream input = new FileInputStream(itemFile);
            Yaml yaml = new Yaml(new Constructor(ItemInfo.class));
            info = (ItemInfo)yaml.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (info != null) {
//            info.fileName = itemFile.getName().substring(0, itemFile.getName().length() - 4);
//            info.packageName = packageName;
//            info.name = info.packageName +  '.' + info.fileName;

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

            if (info.plant != null) {
                info.isPlant = true;
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

        pass1(data);
        pass2(data);
//        pass3(data);
        pass4(data);

        if (_hasErrors) {
            throw new RuntimeException("Errors loading items");
        }
    }

    private void error(ItemInfo item, String message) {
        _hasErrors = true;
        Log.error(message + " (" + item.name + ")");
    }

    private void pass1(GameData data) {
        for (ItemInfo item: data.items) {

            if (item.receipts != null) {
                for (ItemInfo.ItemInfoReceipt receipt: item.receipts) {
                    for (ItemInfo.ItemComponentInfo component: receipt.components) {
                        component.item = data.getItemInfo(component.itemName);
                    }
                    for (ItemInfo.ItemProductInfo product: receipt.products) {
                        product.item = data.getItemInfo(product.itemName);
                    }
                }
            }

            if (item.actions != null) {
                for (ItemInfo.ItemInfoAction action: item.actions) {

                    // Set product items (for self-product item, like rock)
                    if (action.finalProducts != null && !action.finalProducts.isEmpty()) {
                        for (ItemInfo.ItemProductInfo productInfo: action.finalProducts) {
                            productInfo.item = data.getItemInfo(productInfo.itemName);
                            productInfo.rate = productInfo.rate == 0 ? 1 : productInfo.rate;
                        }
                    }
                    if (action.products != null && !action.products.isEmpty()) {
                        for (ItemInfo.ItemProductInfo productInfo: action.products) {
                            productInfo.item = data.getItemInfo(productInfo.itemName);
                            productInfo.rate = productInfo.rate == 0 ? 1 : productInfo.rate;
                        }
                    }

                    // Set receipts (for factory items, like cooker)
                    if (action.receipts != null) {
                        for (ItemInfo.ItemInfoReceipt receiptInfo: action.receipts) {
                            if (receiptInfo.products != null) {
                                for (ItemInfo.ItemProductInfo productInfo : receiptInfo.products) {
                                    productInfo.item = data.getItemInfo(productInfo.itemName);
                                }
                            }
                            if (receiptInfo.components != null) {
                                for (ItemInfo.ItemComponentInfo componentInfo : receiptInfo.components) {
                                    componentInfo.item = data.getItemInfo(componentInfo.itemName);
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
                                throw new RuntimeException("onAction type \"use\" need to be unique");
                            }
                            break;

                        case "cook":
                            break;

                        case "gather":
                            if (item.actions.size() > 1) {
                                throw new RuntimeException("onAction type \"gather\" need to be unique");
                            }
                            data.gatherItems.add(item);
                            break;

                        case "mine":
                            if (item.actions.size() > 1) {
                                throw new RuntimeException("onAction type \"mine\" need to be unique");
                            }
//                            data.gatherItems.add(item);
                            break;
                    }
                }
            }
        }
    }

    private void pass2(GameData data) {
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
            if (item.components != null) {
                for (ItemInfo.ItemComponentInfo component: item.components) {
                    component.item = data.getItemInfo(component.itemName);
                }
            }
        }
    }
//
//    private void pass3(GameData data) {
//        for (ItemInfo item: data.items) {
//            if (item.parent != null) {
//                item.parentInfo = data.getItemInfo(item.parent);
//                item.parentInfo.childs.add(item);
//            }
//        }
//    }

    private void pass4(GameData data) {
//        for (ItemInfo item: data.items) {
//            TextureData textureData = ((GDXSpriteModel) GDXSpriteManager.getInstance().getIcon(item)).getData().getTexture().getTextureData();
//            textureData.prepare();
//            Pixmap pixmap = textureData.consumePixmap();
//            //pixmap.
//            textureData.disposePixmap();
//        }
    }

}
