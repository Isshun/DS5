package alone.in.deepspace.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ItemLoader {

	public static class TestItemCost {
		public int matter;
		public int power;
		public int o2;
	}

	public static class TestItem {
		public String 		name;
		public String 		label;
		public boolean 		solid;
		public int 			width;
		public int 			height;
		public int 			light;
		public TestItemCost cost;
	}
	
	public static void load(String packageName) {
	    System.out.println("load item...");

	    List<TestItem> items = new ArrayList<TestItem>();

	    // List files
		File itemFiles[] = (new File("data/items/")).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.contains(".yml");
			}
		});
		
		// Load files
		for (File itemFile: itemFiles) {
			try {
				InputStream input = new FileInputStream(itemFile);
			    Yaml yaml = new Yaml(new Constructor(TestItem.class));
			    TestItem test = (TestItem)yaml.load(input);
			    test.name = packageName +  '.' + test.name;
			    items.add(test);
			    System.out.println(" - load: " + test.name);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	    System.out.println("items loaded: " + items.size());
	    
	    System.exit(0);

	}

}
