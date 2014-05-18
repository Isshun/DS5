package alone.in.deepspace.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.World.ItemInfo;
import alone.in.deepspace.model.CategoryInfo;

public class CategoryLoader {

	public static void load() {
		Map<String, CategoryInfo> categories = new HashMap<String, CategoryInfo>();
		
		for (ItemInfo itemInfo: ServiceManager.getData().items) {
			String categoryName = itemInfo.category != null ? itemInfo.category : "default";
			CategoryInfo category = categories.get(categoryName); 
			if (category == null) {
				category = new CategoryInfo(categoryName, categoryName);
				categories.put(categoryName, category);
			}
			category.items.add(itemInfo);
		}
		
		System.out.println("category loaded: " + categories.size());
		
		ServiceManager.getData().categories = new ArrayList<CategoryInfo>(categories.values());
	}

}
