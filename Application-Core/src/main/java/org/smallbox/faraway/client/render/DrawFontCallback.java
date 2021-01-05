package org.smallbox.faraway.client.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface DrawFontCallback {
    void onDraw(SpriteBatch batch, BitmapFont font);
}
