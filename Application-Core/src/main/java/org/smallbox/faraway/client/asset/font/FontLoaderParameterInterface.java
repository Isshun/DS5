package org.smallbox.faraway.client.asset.font;

import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public interface FontLoaderParameterInterface {
    FreetypeFontLoader.FreeTypeFontLoaderParameter getParameter(String font, int fontSize);
}
