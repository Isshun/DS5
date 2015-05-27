package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.engine.serializer.WorldSaver.WorldSave;

public interface SerializerInterface {
	void save(WorldSave save);
	void load(WorldSave save);
}
