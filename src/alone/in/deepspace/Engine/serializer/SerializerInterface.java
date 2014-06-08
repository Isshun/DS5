package alone.in.deepspace.engine.serializer;

import alone.in.deepspace.engine.serializer.WorldSaver.WorldSave;

public interface SerializerInterface {
	void save(WorldSave save);
	void load(WorldSave save);
}
