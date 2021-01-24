package org.smallbox.faraway.game.room;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.save.SQLManager;

@GameObject
public class RoomSerializer extends GameSerializer {

    @Override
    public void onSave(SQLManager sqlManager) {
    }

    @Override
    public void onLoad(SQLManager sqlManager) {
    }

}