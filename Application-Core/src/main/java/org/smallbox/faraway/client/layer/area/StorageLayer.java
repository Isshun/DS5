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
import org.smallbox.faraway.game.storage.StorageModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.AREA_LAYER_LEVEL, visible = true)
public class StorageLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private StorageModule storageModule;

    private TextureRegion[] _regions;
    private TextureRegion[] _regionsSelected;

    @OnGameLayerBegin
    public void onGameLayerInit() {
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 0, TILE_SIZE, TILE_SIZE);
        _regions[1] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, TILE_SIZE, TILE_SIZE, TILE_SIZE);
        _regions[2] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, TILE_SIZE * 2, TILE_SIZE, TILE_SIZE);
        _regions[3] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, TILE_SIZE * 3, TILE_SIZE, TILE_SIZE);
        _regions[4] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, TILE_SIZE * 4, TILE_SIZE, TILE_SIZE);
        _regionsSelected = new TextureRegion[5];
        _regionsSelected[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
        _regionsSelected[1] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE);
        _regionsSelected[2] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), TILE_SIZE, TILE_SIZE * 2, TILE_SIZE, TILE_SIZE);
        _regionsSelected[3] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), TILE_SIZE, TILE_SIZE * 3, TILE_SIZE, TILE_SIZE);
        _regionsSelected[4] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), TILE_SIZE, TILE_SIZE * 4, TILE_SIZE, TILE_SIZE);
    }

    @Override
    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        storageModule.getAreas().stream().flatMap(area -> area.getParcels().stream()).forEach(parcel -> renderer.drawTextureRegionOnMap(parcel, _regions[0]));
    }

    public boolean isMandatory() {
        return true;
    }

}
