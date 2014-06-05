package alone.in.deepspace.engine.loader;

import alone.in.deepspace.engine.loader.WorldSaver.WorldSave;

public interface SerializerInterface {
	void save(WorldSave save);
	void load(WorldSave save);
}
