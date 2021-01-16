package org.smallbox.faraway.client.render.layer.ground.impl;

import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.module.world.model.Parcel;

import java.util.Optional;

@GameObject
public class GroundTileGenerator {

    @Inject private AssetManager assetManager;

    public Texture getTexture(Parcel parcel) {
        return Optional.ofNullable(parcel.getGroundInfo().getGraphicInfo(GraphicInfo.Type.TERRAIN))
                .map(graphicInfo -> assetManager.lazyLoad("data" + graphicInfo.path, Texture.class))
                .orElse(null);
    }

}
