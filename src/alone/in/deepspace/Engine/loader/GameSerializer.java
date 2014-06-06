package alone.in.deepspace.engine.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.yaml.snakeyaml.Yaml;

import alone.in.deepspace.engine.loader.WorldSaver.WorldSave;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.util.Log;

import com.thoughtworks.xstream.XStream;

public class GameSerializer {

	public static void load(String filePath, LoadListener loadListener) {
		System.gc();

		loadListener.onUpdate("load savegame [" + filePath + "]");

		Log.info("Load savegame [" + filePath + "]");
	    long time = System.currentTimeMillis();
        
        // Open XML
	    WorldSave worldSave = null;
		try {
			InputStream input = new FileInputStream(filePath + ".xml");
			XStream xstream = new XStream();
			worldSave = (WorldSave)xstream.fromXML(input);

			Log.info("load complete 2: " + (System.currentTimeMillis() - time) + "ms");
		    input.close();
			System.gc();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Load game
		if (worldSave != null) {
			if (worldSave.characters != null) {
				loadListener.onUpdate("Loading characters");
				(new CharacterSerializer()).load(worldSave);
			}

			if (worldSave.rooms != null) {
				loadListener.onUpdate("Loading rooms");
				(new RoomSerializer()).load(worldSave);
			}

			if (worldSave.areas != null) {
				loadListener.onUpdate("Loading areas");
				WorldSaver.load(ServiceManager.getWorldMap(), worldSave.areas);
			}
		}

		loadListener.onUpdate("Load complete");
	    
	    Log.info("load complete: " + (System.currentTimeMillis() - time) + "ms");
        
		System.gc();		
	}
	
	public static void save(String filePath) {
		System.gc();

		// Construct save object
		WorldSave save = new WorldSave();
		(new RoomSerializer()).save(save);
		(new CharacterSerializer()).save(save);
		JobManagerLoader.save(JobManager.getInstance());
		WorldSaver.save(ServiceManager.getWorldMap(), save.areas);
		
		// Write XML
		XStream xstream = new XStream();
		String xml = xstream.toXML(save);
		
		Yaml yaml = new Yaml();
		String yml = yaml.dump(save);
		
		try {
			FileOutputStream fs = new FileOutputStream(filePath + ".xml");
			OutputStreamWriter output = new OutputStreamWriter(fs);
			output.write(xml);
			output.close();
		    fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
