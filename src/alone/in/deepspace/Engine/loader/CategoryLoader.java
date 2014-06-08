package alone.in.deepspace.engine.loader;

import java.util.ArrayList;
import java.util.HashMap;

import alone.in.deepspace.model.CategoryInfo;
import alone.in.deepspace.model.GameData;
import alone.in.deepspace.model.item.ItemInfo;

public class CategoryLoader {
	private HashMap<String, CategoryInfo> 	_categories;
	private ArrayList<Character> 			_usedShortcut;

	public static void load(GameData data) {
		(new CategoryLoader()).init(data);
	}

	// TODO
	private CategoryLoader() {
		_categories = new HashMap<String, CategoryInfo>();
		_usedShortcut = new ArrayList<Character>();
	}
	
	private void init(GameData data) {
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

		System.out.println("category loaded: " + _categories.size());
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
