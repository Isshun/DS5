package org.smallbox.faraway.client.font;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class OutlinedFontLoaderParameter implements FontLoaderParameterInterface {

    @Override
    public FreetypeFontLoader.FreeTypeFontLoaderParameter getParameter(String font, int fontSize) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameter.fontFileName = "data/fonts/" + font + ".ttf";
        parameter.fontParameters.size = fontSize;
        parameter.fontParameters.flip = true;
        parameter.fontParameters.borderColor = Color.BLACK;
        parameter.fontParameters.borderWidth = fontSize / 16f;
        return parameter;
    }

}
