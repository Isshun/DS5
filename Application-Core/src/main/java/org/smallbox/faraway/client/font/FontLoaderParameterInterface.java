package org.smallbox.faraway.client.font;

import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public interface FontLoaderParameterInterface {
    FreetypeFontLoader.FreeTypeFontLoaderParameter getParameter(int fontSize);
}
