package org.smallbox.faraway.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.GameDisplay;
import org.smallbox.faraway.core.engine.renderer.SpriteManager;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.Collection;

/**
 * Created by Alex on 17/06/2015.
 */
public class RoomRenderer extends GameDisplay {
    private final SpriteManager             _spriteManager;
    private final TextureRegion[]           _regions;
    private final TextureRegion[]           _regionsSelected;
    private final Collection<RoomModel>     _roomList;

    public RoomRenderer() {
        _roomList = ((RoomModule)ModuleManager.getInstance().getModule(RoomModule.class)).getRooms();
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
                if (!room.isExterior() && room.getFloor() == WorldHelper.getCurrentFloor()) {
                    synchronized (room.getParcels()) {
                        TextureRegion texture = _regions[0];
//                        TextureRegion texture = UserInterface.getInstance().getSelector().getSelectedRoom() == parcel.getRoom() ? _regionsSelected[0] : _regions[0];
                        for (ParcelModel parcel : room.getParcels()) {
                            renderer.drawOnMap(texture, parcel.x, parcel.y);
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
    public String getName() {
        return "rooms";
    }
}
