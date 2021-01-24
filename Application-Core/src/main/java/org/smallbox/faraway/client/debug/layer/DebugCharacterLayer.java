package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.character.model.base.CharacterPersonalsExtra;

import java.util.List;
import java.util.stream.Collectors;

@GameObject
@GameLayer(level = 999, visible = false)
public class DebugCharacterLayer extends BaseMapLayer {
    @Inject private CharacterModule characterModule;

    private static final Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    private int _index;

    private CharacterModel _character;

    @Override
    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _index = 0;
        renderer.drawRectangle(0, 0, 2000, 2000, BG_COLOR);

        if (_character != null) {
            if (_character.hasExtra(CharacterPersonalsExtra.class)) {
                drawDebug(renderer, "Name", _character.getExtra(CharacterPersonalsExtra.class).getName());
            }
            drawDebug(renderer, "Parcel", _character.getParcel() != null ? _character.getParcel() : "--");
            drawDebug(renderer, "Job", _character.getJob() != null ? _character.getJob() : "--");
            drawDebug(renderer, "Inventory2", _character.hasExtra(CharacterInventoryExtra.class) ? "--" :
                    _character.getExtra(CharacterInventoryExtra.class).getAll().entrySet().stream()
                            .map(entry -> entry.getKey().label + "x" + entry.getValue())
                            .collect(Collectors.joining(", ")));
        }
    }

    @Override
    public boolean onClickOnParcel(List<Parcel> parcels) {
        parcels.forEach(parcel -> {
            CharacterModel character = characterModule.getCharacter(parcel);
            if (character != null) {
                _character = character;
            }
        });
        return false;
    }

    @GameShortcut(key = Input.Keys.F11)
    public void onToggleVisibility() {
            toggleVisibility();
    }

    private void drawDebug(BaseRenderer renderer, String label, Object object) {
        renderer.drawText(12, (_index * 20) + 12, "[" + label.toUpperCase() + "] " + object, Color.BLACK, 18);
        renderer.drawText(11, (_index * 20) + 11, "[" + label.toUpperCase() + "] " + object, Color.BLACK, 18);
        renderer.drawText(10, (_index * 20) + 10, "[" + label.toUpperCase() + "] " + object, Color.WHITE, 18);
        _index++;
    }
}