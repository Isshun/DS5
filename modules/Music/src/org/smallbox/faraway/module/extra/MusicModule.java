package org.smallbox.faraway.module.extra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 05/07/2015.
 */
public class MusicModule extends ModuleBase {
    private Music music;

    @Override
    protected void onLoaded(Game game) {
        music = Gdx.audio.newMusic(Gdx.files.internal("data/musics/Clean Soul.mp3"));
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

    @Override
    public boolean hasOwnThread() { return true; }

    @Override
    public void onGamePaused() {
        if (music != null) {
            music.pause();
        }
    }

    @Override
    public void onGameResume() {
        if (music != null) {
            music.play();
        }
    }
}
