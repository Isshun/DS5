package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

@ApplicationObject
public class BackgroundMusicManager {

    private Music music;

    @OnInit
    private void init() {
        if (music != null) {
            music.dispose();
        }

        music = Gdx.audio.newMusic(Gdx.files.internal("data/musics/Western Inside Loop.wav"));
        music.setLooping(true);
        music.setVolume(0.5f);
    }

    @OnGameLayerInit
    public void start() {
        music.play();
    }

}
