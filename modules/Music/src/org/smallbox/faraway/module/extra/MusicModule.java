package org.smallbox.faraway.module.extra;

import com.badlogic.gdx.audio.Music;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 05/07/2015.
 */
public class MusicModule extends GameModule {
    private Music music;

    @Override
    protected void onGameStart(Game game) {
//        music = Gdx.audio.newMusic(Gdx.files.internal("data/musics/Clean Soul.mp3"));
//        music.play();
//        music.setOnCompletionListener(Music::dispose);
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
