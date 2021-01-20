package org.smallbox.faraway.client.font;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.log.Log;

@ApplicationObject
public class FontManager {
    private final static int MIN_SIZE = 10;
    private final static int MAX_SIZE = 60;

    @Inject private AssetManager assetManager;
    @Inject private RegularFontLoaderParameter regularFontLoaderParameter;
    @Inject private OutlinedFontLoaderParameter outlinedFontLoaderParameter;

    public void generateFonts() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        for (int i = MIN_SIZE; i <= MAX_SIZE; i++) {
            assetManager.load("data/fonts/regular-" + i + "-font.ttf", BitmapFont.class, regularFontLoaderParameter.getParameter("font", i));
            assetManager.load("data/fonts/outlined-" + i + "-font.ttf", BitmapFont.class, outlinedFontLoaderParameter.getParameter("font", i));
        }
    }

    public BitmapFont getFont(int fontSize) {
        return getFont(null, fontSize);
    }

    public BitmapFont getFont(String font, int fontSize) {
        return lazyLoad("data/fonts/" + safeFontName(font) + "-regular-" + Math.max(fontSize, 1) + "-font.ttf", Math.max(fontSize, 1), font, regularFontLoaderParameter);
    }

    public BitmapFont getOutlinedFont(int fontSize) {
        return getOutlinedFont(null, fontSize);
    }

    public BitmapFont getOutlinedFont(String font, int fontSize) {
        return lazyLoad("data/fonts/" + safeFontName(font) + "-outlined-" + fontSize + "-font.ttf", fontSize, font, outlinedFontLoaderParameter);
    }

    private BitmapFont lazyLoad(String key, int fontSize, String font, FontLoaderParameterInterface fontLoaderParameterInterface) {
        if (!assetManager.contains(key)) {
            assetManager.load(key, BitmapFont.class, fontLoaderParameterInterface.getParameter(safeFontName(font), fontSize));
            assetManager.finishLoading();
            Log.warning("Lazy load: " + key);
        }

        return assetManager.get(key);
    }

    private String safeFontName(String fontName) {
        return fontName == null ? "font" : fontName;
    }

}
