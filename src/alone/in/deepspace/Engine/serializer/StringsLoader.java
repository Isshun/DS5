package alone.in.deepspace.engine.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import alone.in.deepspace.Strings;
import alone.in.deepspace.model.GameData;

public class StringsLoader {

	public static void load(GameData data, String path, String fileName) {
	    System.out.println("load strings...");

	    File itemFile = new File(path + fileName + ".yml");
		try {
			InputStream input = new FileInputStream(itemFile);
		    Yaml yaml = new Yaml();
		    @SuppressWarnings("unchecked")
			Map<String, String> map = (HashMap<String, String>)yaml.load(input);

		    if (map.containsKey("PRODUCT_WHEN_GATHERED")) Strings.PRODUCT_WHEN_GATHERED = map.get("PRODUCT_WHEN_GATHERED");
		    if (map.containsKey("PRODUCT_WHEN_MINED")) Strings.PRODUCT_WHEN_MINED = map.get("PRODUCT_WHEN_MINED");
		    if (map.containsKey("PROVIDE")) Strings.PROVIDE = map.get("PROVIDE");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	    System.out.println("strings loaded");
	}
}
