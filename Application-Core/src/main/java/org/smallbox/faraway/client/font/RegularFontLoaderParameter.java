package org.smallbox.faraway.client.font;

import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class RegularFontLoaderParameter implements FontLoaderParameterInterface {

    @Override
    public FreetypeFontLoader.FreeTypeFontLoaderParameter getParameter(int fontSize) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameter.fontFileName = "data/fonts/font.ttf";
        parameter.fontParameters.size = fontSize;
        parameter.fontParameters.flip = true;
        return parameter;
    }

}
