package org.smallbox.faraway.client.render.layer.area;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaTypeInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerManager.AREA_LAYER_LEVEL, visible = true)
public class AreaLayer extends BaseLayer {

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private AreaModule areaModule;

    private final Map<Class, TextureRegion> _textureByClass = new ConcurrentHashMap<>();
    private TextureRegion[] _regions;
    private TextureRegion[] _regionsSelected;
    private int _mouseX;
    private int _mouseY;

    public enum Mode {NONE, ADD, SUB}

    private Mode _mode;
    private Class<? extends AreaModel> _cls;

    private final Color[] COLORS = new Color[] {
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    @Override
    public void onGameStart(Game game) {
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
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = -viewport.getPosX() / TILE_SIZE;
        int fromY = -viewport.getPosY() / TILE_SIZE;
        int toX = fromX + viewport.getWidth() / TILE_SIZE;
        int toY = fromY + viewport.getHeight() / TILE_SIZE;

        areaModule.getAreas().forEach(area ->
                area.getParcels().forEach(parcel ->
                        renderer.drawOnMap(parcel.x, parcel.y, getTexture(area.getClass()))));

        if (_mode == Mode.ADD) {
            renderer.drawText(_mouseX - 20, _mouseY - 20, 16, Color.CHARTREUSE, "Add " + _cls.getAnnotation(AreaTypeInfo.class).label() + " area");
        }

        if (_mode == Mode.SUB) {
            renderer.drawText(_mouseX - 20, _mouseY - 20, 16, Color.CHARTREUSE, "Sub " + _cls.getAnnotation(AreaTypeInfo.class).label() + " area");
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
