package org.smallbox.faraway.core.data.serializer;

import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import org.smallbox.faraway.core.game.GameInfo;

import java.io.FileOutputStream;
import java.io.IOException;

public interface SerializerInterface {
    void save(FileOutputStream save) throws IOException;
    void load(GameInfo gameInfo, VTDNav vn, GameSerializer.GameSerializerInterface gameSerializerInterface) throws XPathParseException, NavException, XPathEvalException;
}
