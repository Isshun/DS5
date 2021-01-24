package org.smallbox.faraway.client.render.layer.area;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.BaseRendererManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.plant.GardenModule;
import org.smallbox.faraway.util.Constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
@GameLayer(level = LayerManager.AREA_LAYER_LEVEL, visible = true)
public class GardenLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private GardenModule gardenModule;

    private final Map<Class, TextureRegion> _textureByClass = new ConcurrentHashMap<>();
    private TextureRegion[] _regions;
    private TextureRegion[] _regionsSelected;
    private int _mouseX;
    private int _mouseY;

    public enum Mode {NONE, ADD, SUB}

    private Mode _mode;
    private Class<? extends AreaModel> _cls;

    @Override
    public void onGameStart(Game game) {
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 32, 32, 32);
        _regions[1] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 32, 32, 32);
        _regions[2] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 64, 32, 32);
        _regions[3] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 96, 32, 32);
        _regions[4] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 128, 32, 32);
        _regionsSelected = new TextureRegion[5];
        _regionsSelected[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 0, 32, 32);
        _regionsSelected[1] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 32, 32, 32);
        _regionsSelected[2] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 64, 32, 32);
        _regionsSelected[3] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 96, 32, 32);
        _regionsSelected[4] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 32, 128, 32, 32);
    }

    @Override
    public void    onDraw(BaseRendererManager renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = -viewport.getPosX() / Constant.TILE_SIZE;
        int fromY = -viewport.getPosY() / Constant.TILE_SIZE;
        int toX = fromX + viewport.getWidth() / Constant.TILE_SIZE;
        int toY = fromY + viewport.getHeight() / Constant.TILE_SIZE;

        gardenModule.getAreas().stream().flatMap(area -> area.getParcels().stream()).forEach(parcel -> renderer.drawTextureRegionOnMap(parcel, _regions[0]));

        if (_mode == Mode.ADD) {
            renderer.drawText(_mouseX - 20, _mouseY - 20, "Add " + _cls.getAnnotation(AreaTypeInfo.class).label() + " area", Color.CHARTREUSE, 16);
        }

        if (_mode == Mode.SUB) {
            renderer.drawText(_mouseX - 20, _mouseY - 20, "Sub " + _cls.getAnnotation(AreaTypeInfo.class).label() + " area", Color.CHARTREUSE, 16);
        }
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

    public void setMode(Mode mode, Class cls) {
        _mode = mode;
        _cls = cls;
    }
}
