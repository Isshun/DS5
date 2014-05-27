package alone.in.deepspace.engine.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.CategoryInfo;
import alone.in.deepspace.model.ItemInfo;

public class CategoryLoader {

	public static void load() {
		Map<String, CategoryInfo> categories = new HashMap<String, CategoryInfo>();
		
		for (ItemInfo itemInfo: ServiceManager.getData().items) {
			String categoryName = itemInfo.room != null ? itemInfo.room : "default";
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
