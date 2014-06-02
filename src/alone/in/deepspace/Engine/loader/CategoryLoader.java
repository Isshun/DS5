package alone.in.deepspace.engine.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.CategoryInfo;
import alone.in.deepspace.model.item.ItemInfo;

public class CategoryLoader {
	private List<CategoryInfo> 				_sortedCategories;
	private HashMap<String, CategoryInfo> 	_categories;
	private ArrayList<Character> 			_usedShortcut;

	public static void load() {
		(new CategoryLoader()).init();
	}

	private CategoryLoader() {
		_sortedCategories = new ArrayList<CategoryInfo>();
		ServiceManager.getData().categories = _sortedCategories; 
		_categories = new HashMap<String, CategoryInfo>();
		_usedShortcut = new ArrayList<Character>();
	}
	
	private void init() {
		getOrCreateCategory("structure");
		
		for (ItemInfo itemInfo: ServiceManager.getData().items) {
			String categoryName = "default";
			if (itemInfo.isStructure) {
				categoryName = "structure";
			} else if (itemInfo.room != null) {
				categoryName = itemInfo.room;
			}

			CategoryInfo category = getOrCreateCategory(categoryName);
			category.items.add(itemInfo);
		}

		System.out.println("category loaded: " + _categories.size());
	}

	private CategoryInfo getOrCreateCategory(String categoryName) {
		if (_categories.containsKey(categoryName)) {
			return _categories.get(categoryName);
		}

		// Create new category
		CategoryInfo category = new CategoryInfo(categoryName, categoryName);
		category.shortcutPos = getShortcutPos(categoryName);
		if (category.shortcutPos != -1) {
			category.shortcut = String.valueOf(categoryName.charAt(category.shortcutPos));
			category.labelWithoutShortcut = categoryName.substring(0, category.shortcutPos) + " " + categoryName.substring(category.shortcutPos + 1);
		} else {
			category.labelWithoutShortcut = categoryName;
		}
		_categories.put(categoryName, category);
		_sortedCategories.add(category);
		
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
