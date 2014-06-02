package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryInfo {
	public List<ItemInfo> 	items;
	public String			name;
	public String			label;
	public int 				shortcutPos;
	public String 			shortcut;
	public String 			labelWithoutShortcut;
	
	public CategoryInfo(String name, String label) {
		this.items = new ArrayList<ItemInfo>();
		this.name = name;
		this.label = label;
	}
}
