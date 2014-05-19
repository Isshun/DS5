package alone.in.deepspace.engine.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.ItemInfo;

public class ItemLoader {
	
	public static void load(String path, String packageName) {
	    System.out.println("load items...");

	    List<ItemInfo> items = new ArrayList<ItemInfo>();
	    
	    // List files
		File itemFiles[] = (new File(path)).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.contains(".yml");
			}
		});
		
		// Load files
		for (File itemFile: itemFiles) {
			try {
				InputStream input = new FileInputStream(itemFile);
			    Yaml yaml = new Yaml(new Constructor(ItemInfo.class));
			    ItemInfo info = (ItemInfo)yaml.load(input);
			    info.fileName = itemFile.getName().substring(0, itemFile.getName().length() - 4);
			    info.packageName = packageName;
			    info.name = info.packageName +  '.' + info.fileName;
			    info.isWalkable = true;
			    if (!info.isStructure && !info.isRessource) {
			    	info.isUserItem = true;
			    }
			    items.add(info);
			    System.out.println(" - load: " + info.name);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	    System.out.println("items loaded: " + items.size());
	    
	    ServiceManager.getData().items.addAll(items);
	}

}
