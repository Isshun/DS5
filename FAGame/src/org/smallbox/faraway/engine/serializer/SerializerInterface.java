package org.smallbox.faraway.engine.serializer;

public interface SerializerInterface {
	void save(GameSerializer.GameSave save);
	void load(GameSerializer.GameSave save);
}
