package org.smallbox.faraway.client.font;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class FontManager {
    private final static int MIN_SIZE = 10;
    private final static int MAX_SIZE = 40;

    @Inject private SpriteManager spriteManager;
    @Inject private RegularFontLoaderParameter regularFontLoaderParameter;
    @Inject private OutlinedFontLoaderParameter outlinedFontLoaderParameter;

    public void generateFonts() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        spriteManager.getAssetManager().setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        spriteManager.getAssetManager().setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        for (int i = MIN_SIZE; i <= MAX_SIZE; i++) {
            spriteManager.getAssetManager().load("data/fonts/regular-" + i + "-font.ttf", BitmapFont.class, regularFontLoaderParameter.getParameter(i));
            spriteManager.getAssetManager().load("data/fonts/outlined-" + i + "-font.ttf", BitmapFont.class, outlinedFontLoaderParameter.getParameter(i));
        }

        spriteManager.getAssetManager().finishLoading();
    }

    public BitmapFont getFont(int fontSize) {
        return lazyLoad("data/fonts/regular-" + Math.max(fontSize, 1) + "-font.ttf", Math.max(fontSize, 1), regularFontLoaderParameter);
    }

    public BitmapFont getOutlinedFont(int fontSize) {
        return lazyLoad("data/fonts/outlined-" + fontSize + "-font.ttf", fontSize, outlinedFontLoaderParameter);
    }

    private BitmapFont lazyLoad(String key, int fontSize, FontLoaderParameterInterface fontLoaderParameterInterface) {
        if (!spriteManager.getAssetManager().contains(key)) {
            spriteManager.getAssetManager().load(key, BitmapFont.class, fontLoaderParameterInterface.getParameter(fontSize));
            spriteManager.getAssetManager().finishLoading();
        }

        return spriteManager.getAssetManager().get(key);
    }

}
