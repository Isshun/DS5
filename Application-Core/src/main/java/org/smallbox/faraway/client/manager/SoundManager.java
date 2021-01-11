package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

@ApplicationObject
public class SoundManager {

    private Sound sound;

    @OnInit
    private void init() {
        sound = Gdx.audio.newSound(Gdx.files.internal("data/sounds/gathering_stone.wav"));
    }

    public long start() {
        long soundId = sound.play();
        sound.setLooping(soundId, true);
        return soundId;
    }

    public void stop(long soundId) {
        sound.stop(soundId);
    }
}
