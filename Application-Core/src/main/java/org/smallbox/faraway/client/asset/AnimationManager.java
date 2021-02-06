package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationObject
public class AnimationManager {
    @Inject private PixmapManager pixmapManager;

    private final Map<String, Animation<TextureRegion>> animations = new ConcurrentHashMap<>();

    public void init(String key, String absolutPath, String regionName, float frameDuration, Animation.PlayMode playMode) {
        createAnimation(key, absolutPath, regionName, frameDuration, playMode, false);
        createAnimation(key + "-overlay", absolutPath, regionName, frameDuration, playMode, true);
    }

    public void createAnimation(String key, String absolutPath, String regionName, float frameDuration, Animation.PlayMode playMode, boolean overlay) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(absolutPath));
        atlas.getTextures().forEach(texture -> texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear));
        atlas.getRegions().forEach(region -> region.flip(false, true));

        Animation<TextureRegion> animation = new Animation<>(frameDuration, atlas.findRegions(regionName), playMode);

        if (overlay) {
            for (TextureRegion textureRegion : animation.getKeyFrames()) {
                textureRegion.setTexture(pixmapManager.createOverlay(textureRegion.getTexture(), "player"));
            }
        }

        animations.put(key, animation);
    }

    public TextureRegion getFrame(String key, float stateTime) {
        return animations.get(key).getKeyFrame(stateTime);
    }
}
