package org.smallbox.faraway.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class FontManager {
    private final static int MAX_SIZE = 100;

    private BitmapFont[] fonts = new BitmapFont[MAX_SIZE + 2];
    private BitmapFont[] outlinedFonts = new BitmapFont[MAX_SIZE + 2];

    public void generateFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/font.ttf"));
        for (int i = 5; i <= MAX_SIZE; i++) {
            fonts[i] = generateNormalFont(generator, i);
            outlinedFonts[i] = generateOutlinedFont(generator, i);
        }
        fonts[MAX_SIZE + 1] = generateNormalFont(generator, 200);
        outlinedFonts[MAX_SIZE + 1] = generateOutlinedFont(generator, 200);
        generator.dispose();
    }

    private BitmapFont generateNormalFont(FreeTypeFontGenerator generator, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.flip = true;
        return generator.generateFont(parameter);
    }

    private BitmapFont generateOutlinedFont(FreeTypeFontGenerator generator, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = (size / 12f);
        parameter.size = size;
        parameter.flip = true;
        return generator.generateFont(parameter);
    }

    public BitmapFont getFont(int textSize) {
        return fonts[textSize];
    }

    public BitmapFont getOutlinedFont(int textSize) {
        return outlinedFonts[textSize];
    }

}
