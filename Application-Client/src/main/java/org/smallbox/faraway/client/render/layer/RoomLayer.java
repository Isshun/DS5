package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.RoomModel;
import org.smallbox.faraway.modules.room.model.RoomTypeInfo;
import org.smallbox.faraway.util.Constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
@GameLayer(level = LayerManager.ROOM_LAYER_LEVEL, visible = true)
public class RoomLayer extends BaseLayer {

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private RoomModule roomModule;

    private Map<Class, TextureRegion> _textureByClass = new ConcurrentHashMap<>();
    private TextureRegion[] _regions;
    private TextureRegion[] _regionsSelected;
    private int _mouseX;
    private int _mouseY;

    public enum Mode {NONE, ADD, SUB}

    private Mode _mode;
    private Class<? extends RoomModel> _cls;

    private Color[] COLORS = new Color[] {
            new Color(0.5f, 0.5f, 1f, 0.4f),
            new Color(1, 1, 0, 0.4f),
            new Color(1, 0, 1, 0.4f),
            new Color(0, 1, 1, 0.4f),
            new Color(1, 0.5f, 0.5f, 0.4f)
    };

    @Override
    public void onGameStart(Game game) {
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(spriteManager.getTexture("data/res/bg_area.png"), 0, 0, 32, 32);
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
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = -viewport.getPosX() / Constant.TILE_WIDTH;
        int fromY = -viewport.getPosY() / Constant.TILE_HEIGHT;
        int toX = fromX + viewport.getWidth() / Constant.TILE_WIDTH;
        int toY = fromY + viewport.getHeight() / Constant.TILE_HEIGHT;

        roomModule.getRooms().forEach(Room ->
                Room.getParcels().forEach(parcel ->
                        renderer.drawOnMap(parcel.x, parcel.y, getTexture(Room.getClass()))));

        if (_mode == Mode.ADD) {
            renderer.drawText(_mouseX - 20, _mouseY - 20, 16, Color.CHARTREUSE, "Add " + _cls.getAnnotation(RoomTypeInfo.class).label() + " Room");
        }

        if (_mode == Mode.SUB) {
            renderer.drawText(_mouseX - 20, _mouseY - 20, 16, Color.CHARTREUSE, "Sub " + _cls.getAnnotation(RoomTypeInfo.class).label() + " Room");
        }
    }

    private TextureRegion getTexture(Class<? extends RoomModel> cls) {
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
