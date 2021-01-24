package org.smallbox.faraway.client.debug.dashboard.content;

import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;

import java.util.List;
import java.util.stream.Collectors;

@GameObject
public class TextureAssetsDashboardLayer extends DashboardLayerBase {

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        List<String> entries = assetToList(Texture.class).stream().map(Texture::toString).sorted().collect(Collectors.toList());
        for (String assetName : entries) {
            drawDebug(renderer, "ASSET", assetName);
        }
    }

}
