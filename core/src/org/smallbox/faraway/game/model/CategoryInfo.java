package org.smallbox.faraway.game.model;

import org.smallbox.faraway.game.model.item.ItemInfo;

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
