package org.smallbox.faraway.data.serializer;

public interface SerializerInterface {
	void save(GameSerializer.GameSave save);
	void load(GameSerializer.GameSave save);
}
