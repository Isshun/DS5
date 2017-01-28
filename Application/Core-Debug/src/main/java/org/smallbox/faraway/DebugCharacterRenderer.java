package org.smallbox.faraway;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.dependencyInjector.Component;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.character.CharacterModule;

import java.util.List;
import java.util.stream.Collectors;

@Component
@GameRenderer(level = 999)
public class DebugCharacterRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule characterModule;

    private static Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    private int _index;

    private CharacterModel _character;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        _index = 0;
        renderer.draw(BG_COLOR, 0, 0, 2000, 2000);

        if (_character != null) {
            drawDebug(renderer, "Name", _character.getPersonals() != null ? _character.getPersonals().getName() : "--");
            drawDebug(renderer, "Parcel", _character.getParcel() != null ? _character.getParcel() : "--");
            drawDebug(renderer, "Job", _character.getJob() != null ? _character.getJob() : "--");
            drawDebug(renderer, "Inventory", _character.getInventory() != null ? _character.getInventory() : "--");
            drawDebug(renderer, "Inventory2", _character.getInventory2() == null ? "--" :
                    String.join(", ", _character.getInventory2().entrySet().stream()
                            .map(entry -> entry.getKey().label + "x" + entry.getValue())
                            .collect(Collectors.toList())));
        }
    }

    @Override
    public boolean onClickOnParcel(List<ParcelModel> parcels) {
        parcels.forEach(parcel -> {
            CharacterModel character = characterModule.getCharacter(parcel);
            if (character != null) {
                _character = character;
            }
        });
        return false;
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.F11) {
            toggleVisibility();
        }
    }

    private void drawDebug(GDXRenderer renderer, String label, Object object) {
        renderer.draw("[" + label.toUpperCase() + "] " + object, 18, 12, (_index * 20) + 12, Color.BLACK);
        renderer.draw("[" + label.toUpperCase() + "] " + object, 18, 11, (_index * 20) + 11, Color.BLACK);
        renderer.draw("[" + label.toUpperCase() + "] " + object, 18, 10, (_index * 20) + 10, Color.WHITE);
        _index++;
    }

    public int getLevel() {
        return 999;
    }
}