package org.smallbox.faraway.modules.room;

import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.module.world.SQLManager;

public class RoomSerializer extends GameSerializer<RoomModule> {

    @Override
    public void onSave(SQLManager sqlManager, RoomModule module, Game game) {
    }

    @Override
    public void onLoad(SQLManager sqlManager, RoomModule module, Game game, Data data) {
    }

}