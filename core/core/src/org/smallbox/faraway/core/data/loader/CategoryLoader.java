package org.smallbox.faraway.core.data.loader;

import org.smallbox.faraway.core.game.model.CategoryInfo;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.item.ItemInfo;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryLoader implements IDataLoader {
    private HashMap<String, CategoryInfo>     _categories;
    private ArrayList<Character>             _usedShortcut;

    @Override
    public void reloadIfNeeded(GameData data) {
    }

    @Override
    public void load(GameData data) {
        data.categories =  new ArrayList<>();

        getOrCreateCategory(data, "structure");

        for (ItemInfo itemInfo: data.items) {
            String categoryName = "default";
            if (itemInfo.isStructure) {
                categoryName = "structure";
            } else if (itemInfo.category != null) {
                categoryName = itemInfo.category;
            }

            CategoryInfo category = getOrCreateCategory(data, categoryName);
            category.items.add(itemInfo);
        }

        Log.debug("category loaded: " + _categories.size());
    }

    // TODO
    public CategoryLoader() {
        _categories = new HashMap<>();
        _usedShortcut = new ArrayList<>();
    }

    private CategoryInfo getOrCreateCategory(GameData data, String categoryName) {
        if (_categories.containsKey(categoryName)) {
            return _categories.get(categoryName);
        }

        return createCategory(data, categoryName);
    }

    // Create new category
    private CategoryInfo createCategory(GameData data, String categoryName) {
        CategoryInfo category = new CategoryInfo(categoryName, categoryName);
        category.shortcutPos = getShortcutPos(categoryName);
        if (category.shortcutPos != -1) {
            category.shortcut = String.valueOf(categoryName.charAt(category.shortcutPos));
            category.labelWithoutShortcut = categoryName.substring(0, category.shortcutPos) + " " + categoryName.substring(category.shortcutPos + 1);
        } else {
            category.labelWithoutShortcut = categoryName;
        }
        _categories.put(categoryName, category);
        data.categories.add(category);

        return category;
    }

    private int getShortcutPos(String categoryName) {
        for (int i = 0; i < categoryName.length(); i++) {
            if (_usedShortcut.contains(categoryName.charAt(i)) == false) {
                _usedShortcut.add(categoryName.charAt(i));
                return i;
            }
        }
        return -1;
    }

}
