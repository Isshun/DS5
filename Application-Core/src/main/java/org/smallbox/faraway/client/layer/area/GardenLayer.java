package org.smallbox.faraway.client.layer.area;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;
import org.smallbox.faraway.game.plant.GardenModule;
import org.smallbox.faraway.util.Constant;

@GameObject
@GameLayer(level = LayerLevel.AREA_LAYER_LEVEL, visible = true)
public class GardenLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private GardenModule gardenModule;

    private TextureRegion[] _regions;

    @OnGameLayerBegin
    public void onGameLayerInit() {
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(spriteManager.getTexture("data/graphics/plants/garden.png"), 0, Constant.TILE_SIZE, Constant.TILE_SIZE, Constant.TILE_SIZE);
    }

    @Override
    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        gardenModule.getAreas().stream()
                .flatMap(area -> area.getParcels().stream())
                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> renderer.drawTextureRegionOnMap(parcel, _regions[0]));
    }

    public boolean isMandatory() {
        return true;
    }

}
