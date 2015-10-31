package org.smallbox.faraway.module.extra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.smallbox.faraway.core.module.GameModule;

/**
 * Created by Alex on 05/07/2015.
 */
public class MusicModule extends GameModule {
    @Override
    protected void onLoaded() {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("data/musics/Clean Soul.mp3"));
        music.play();
        music.setOnCompletionListener(Music::dispose);
    }

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
