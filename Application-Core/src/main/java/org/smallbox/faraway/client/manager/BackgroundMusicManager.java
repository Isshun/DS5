package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.apache.commons.lang3.RandomUtils;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

import java.util.Arrays;
import java.util.List;

@ApplicationObject
public class BackgroundMusicManager {

    private List<String> randoms = Arrays.asList(
            "data/musics/Western Inside Loop.wav",
            "data/musics/Western Inside Loop.wav"
    );

    private Music music;

    @OnInit
    private void init() {
        load(getRandom(), false);
    }

    public void start() {
        music.play();
    }

    private void load(String path, boolean autostart) {
        if (music != null) {
            music.dispose();
        }

        music = Gdx.audio.newMusic(Gdx.files.internal(path.replace("[base]", "data")));
        music.setLooping(true);
        music.setVolume(0.5f);

        if (autostart) {
            music.play();
        }
    }

    public void play(String path) {
        load(path, true);
    }

    public void playRandom() {
        load(getRandom(), true);
    }

    private String getRandom() {
        return randoms.stream().skip(RandomUtils.nextInt(0, randoms.size() - 1)).findFirst().orElse(null);
    }

}
