package org.smallbox.faraway.client.debug.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.dependencyInjector.Component;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterPersonalsExtra;

import java.util.List;
import java.util.stream.Collectors;

@Component
@GameRenderer(level = 999, visible = false)
public class DebugCharacterRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule characterModule;

    private static Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    private int _index;

    private CharacterModel _character;

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _index = 0;
        renderer.drawPixel(0, 0, 2000, 2000, BG_COLOR);

        if (_character != null) {
            drawDebug(renderer, "Name", _character.getExtra(CharacterPersonalsExtra.class).getName());
            drawDebug(renderer, "Parcel", _character.getParcel() != null ? _character.getParcel() : "--");
            drawDebug(renderer, "Job", _character.getJob() != null ? _character.getJob() : "--");
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

    @GameShortcut(key = GameEventListener.Key.F11)
    public void onToggleVisibility() {
            toggleVisibility();
    }

    private void drawDebug(GDXRenderer renderer, String label, Object object) {
        renderer.drawText(12, (_index * 20) + 12, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawText(11, (_index * 20) + 11, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawText(10, (_index * 20) + 10, 18, Color.WHITE, "[" + label.toUpperCase() + "] " + object);
        _index++;
    }
}