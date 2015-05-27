package org.smallbox.faraway.engine.serializer;

import com.thoughtworks.xstream.XStream;
import org.smallbox.faraway.engine.serializer.WorldSaver.WorldSave;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;

import java.io.*;

public class GameSerializer {

	public static void load(String filePath, LoadListener loadListener) {
		System.gc();

		loadListener.onUpdate("Load savegame [" + filePath + "]");
		Log.info("Load savegame [" + filePath + "]");
	    long time = System.currentTimeMillis();
        
        // Open XML
	    WorldSave worldSave = null;
		try {
			InputStream input = new FileInputStream(filePath + ".xml");
			XStream xstream = new XStream();
			worldSave = (WorldSave)xstream.fromXML(input);

			Log.info("Save file loaded: " + (System.currentTimeMillis() - time) + "ms");
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

			if (worldSave.areas != null) {
				loadListener.onUpdate("Loading areas");
				WorldSaver.load(ServiceManager.getWorldMap(), worldSave.areas);
			}

			if (worldSave.rooms != null) {
				loadListener.onUpdate("Loading rooms");
				(new RoomSerializer()).load(worldSave);
			}
		}

	    Log.info("Game loaded: " + (System.currentTimeMillis() - time) + "ms");
        
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
