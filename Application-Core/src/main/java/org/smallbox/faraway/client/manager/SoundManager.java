package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.audio.Sound;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

@ApplicationObject
public class SoundManager {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private AssetManager assetManager;

    private Sound sound;

    @OnInit
    private void init() {
        sound = assetManager.lazyLoad("data/sounds/gathering_stone.wav", Sound.class);
    }

    public long start() {
        long soundId = sound.play();
        sound.setVolume(soundId, applicationConfig.soundVolume);
        sound.setLooping(soundId, true);
        return soundId;
    }

    public void stop(long soundId) {
        sound.stop(soundId);
    }
}
