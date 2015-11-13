package org.smallbox.faraway.core.game.module.room;

import com.ximpleware.VTDNav;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.GameInfo;

import java.io.FileOutputStream;

/**
 * Created by Alex on 01/06/2015.
 */
public class RoomModuleSerializer implements SerializerInterface {
    @Override
    public void save(FileOutputStream save) {
    }

    @Override
    public void load(GameInfo gameInfo, VTDNav vn, GameSerializer.GameSerializerInterface gameSerializerInterface) {
    }
}
