package alone.in.deepspace.Models;

import java.util.ArrayList;
import java.util.List;


public class CategoryInfo {
	public List<ItemInfo> 	items;
	public String			name;
	public String			label;
	
	public CategoryInfo(String name, String label) {
		this.items = new ArrayList<ItemInfo>();
		this.name = name;
		this.label = label;
	}
}
