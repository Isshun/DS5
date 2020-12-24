package org.smallbox.faraway.module.music;

import com.badlogic.gdx.audio.Music;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;

public class MusicModule extends GameModule {
    private Music music;

    @Override
    public void onGameStart(Game game) {
//        music = Gdx.audio.newMusic(Gdx.files.internal("data/musics/Clean Soul.mp3"));
//        music.play();
//        music.setOnCompletionListener(Music::dispose);
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

    @Override
    public boolean runOnMainThread() { return true; }

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
