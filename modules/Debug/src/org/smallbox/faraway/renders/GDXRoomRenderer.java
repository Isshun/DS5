package org.smallbox.faraway.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.ui.UserInterface;

import java.util.List;

/**
 * Created by Alex on 17/06/2015.
 */
public class GDXRoomRenderer extends BaseRenderer {
    private final SpriteManager _spriteManager;
    private final TextureRegion[] _regions;
    private final TextureRegion[] _regionsSelected;
    private final List<RoomModel> _roomList;

    public GDXRoomRenderer() {
        _roomList = ((RoomModule) ModuleManager.getInstance().getModule(RoomModule.class)).getRoomList();
        _spriteManager = SpriteManager.getInstance();
        _regions = new TextureRegion[5];
        _regions[0] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 0, 32, 32);
        _regions[1] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 32, 32, 32);
        _regions[2] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 64, 32, 32);
        _regions[3] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 96, 32, 32);
        _regions[4] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 0, 128, 32, 32);
        _regionsSelected = new TextureRegion[5];
        _regionsSelected[0] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 0, 32, 32);
        _regionsSelected[1] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 32, 32, 32);
        _regionsSelected[2] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 64, 32, 32);
        _regionsSelected[3] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 96, 32, 32);
        _regionsSelected[4] = new TextureRegion(_spriteManager.getTexture("data/res/bg_area.png"), 32, 128, 32, 32);
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        synchronized (_roomList) {
            _roomList.stream().forEach(room -> {
                if (!room.isExterior()) {
                    for (ParcelModel parcel : room.getParcels()) {
                        if (UserInterface.getInstance().getSelector().getSelectedArea() == parcel.getArea()) {
                            renderer.drawOnMap(_regionsSelected[0], parcel.x, parcel.y);
                        } else {
                            renderer.drawOnMap(_regions[0], parcel.x, parcel.y);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.room;
    }
}
