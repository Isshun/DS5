package org.smallbox.faraway.client.asset.font;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import groovy.lang.Tuple2;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.log.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationObject
public class FontManager {
    private final static int MIN_SIZE = 10;
    private final static int MAX_SIZE = 25;

    @Inject private AssetManager assetManager;
    @Inject private RegularFontLoaderParameter regularFontLoaderParameter;
    @Inject private OutlinedFontLoaderParameter outlinedFontLoaderParameter;

    private Map<Consumer<BitmapFont>, Tuple2<String, Integer>> futureConsumers = new ConcurrentHashMap<>();

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

    public void futureFont(String font, int fontSize, Consumer<BitmapFont> fontConsumer) {
        futureConsumers.put(fontConsumer, new Tuple2<>(font, fontSize));
    }

    public void resolveFutures() {
        futureConsumers.forEach((fontConsumer, tuple) -> fontConsumer.accept(getFont(tuple.getFirst(), tuple.getSecond())));
        futureConsumers.clear();
    }

    public BitmapFont getFont(String font, int fontSize) {
        return lazyLoad("data/fonts/" + safeFontName(font) + "-regular-" + Math.max(fontSize, 1) + "-font.ttf", Math.max(fontSize, 1), font, regularFontLoaderParameter);
    }

    public BitmapFont getOutlinedFont(String font, int fontSize) {
        return lazyLoad("data/fonts/" + safeFontName(font) + "-outlined-" + fontSize + "-font.ttf", fontSize, font, outlinedFontLoaderParameter);
    }

    private BitmapFont lazyLoad(String key, int fontSize, String font, FontLoaderParameterInterface fontLoaderParameterInterface) {
        if (!assetManager.contains(key)) {
            assetManager.load(key, BitmapFont.class, fontLoaderParameterInterface.getParameter(safeFontName(font), fontSize));
            assetManager.finishLoading();
            Log.debug("Lazy load: " + key);
        }

        return assetManager.get(key);
    }

    private String safeFontName(String fontName) {
        return fontName == null ? "font" : fontName;
    }

}
