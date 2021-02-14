package org.smallbox.faraway.client.layer.area;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.AREA_LAYER_LEVEL, visible = true)
public class AreaLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private AreaModule areaModule;

    private final Map<Class, TextureRegion> _textureByClass = new ConcurrentHashMap<>();
    private TextureRegion[] _regions;
    private TextureRegion[] _regionsSelected;

    private final Color[] COLORS = new Color[] {
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    @OnGameStart
    public void onGameStart() {
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
        areaModule.getAreas().forEach(area ->
                area.getParcels().forEach(parcel ->
                        renderer.drawTextureRegionOnMap(parcel, getTexture(area.getClass()))));

    }

    private TextureRegion getTexture(Class<? extends AreaModel> cls) {
        if (!_textureByClass.containsKey(cls)) {
            _textureByClass.put(cls, _regions[Math.min(_textureByClass.size(), 4)]);
        }

        return _textureByClass.get(cls);
    }

    public boolean isMandatory() {
        return true;
    }

}
