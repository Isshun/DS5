package org.smallbox.faraway.data.serializer;

import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

public interface SerializerInterface {
	void save(GameSerializer.GameSave save);
	void load(VTDNav save) throws XPathParseException, NavException, XPathEvalException;
}
