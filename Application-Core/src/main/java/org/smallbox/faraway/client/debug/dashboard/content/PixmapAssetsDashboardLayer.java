package org.smallbox.faraway.client.debug.dashboard.content;

import com.badlogic.gdx.graphics.Pixmap;
import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;

import java.util.List;
import java.util.stream.Collectors;

@GameObject
public class PixmapAssetsDashboardLayer extends DashboardLayerBase {

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        List<String> entries = assetToList(Pixmap.class).stream().map(Pixmap::toString).sorted().collect(Collectors.toList());
        for (String assetName : entries) {
            drawDebug(renderer, "ASSET", assetName);
        }
    }

}
