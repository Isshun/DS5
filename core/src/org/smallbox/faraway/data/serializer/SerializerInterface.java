package org.smallbox.faraway.data.serializer;

import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

import java.io.FileOutputStream;
import java.io.IOException;

public interface SerializerInterface {
	void save(FileOutputStream save) throws IOException;
	void load(VTDNav save) throws XPathParseException, NavException, XPathEvalException;
}
