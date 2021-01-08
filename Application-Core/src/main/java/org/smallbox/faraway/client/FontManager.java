package org.smallbox.faraway.client;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.FileUtils;

import java.io.File;

@ApplicationObject
public class FontManager {
    private final static int MAX_SIZE = 100;

    private BitmapFont[] fonts;

    public void generateFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        fonts = new BitmapFont[MAX_SIZE + 2];
        for (int i = 5; i <= MAX_SIZE; i++) {
            fonts[i] = fontGen.createFont(new FileHandle(new File(FileUtils.BASE_PATH, "data/fonts/font.ttf")), "font-" + i, i);
            fonts[i].getData().flipped = true;
        }
        fonts[MAX_SIZE + 1] = fontGen.createFont(new FileHandle(new File(FileUtils.BASE_PATH, "data/fonts/font.ttf")), "font-" + 101, 200);
    }

    public BitmapFont getFont(int textSize) {
        return fonts[textSize];
    }
}
