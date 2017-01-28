package org.smallbox.faraway;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.dependencyInjector.Component;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.item.UsableItem;

import java.util.HashMap;
import java.util.Map;

@Component
@GameRenderer(level = 999)
public class DebugRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private ItemModule itemModule;

    @BindModule
    private ConsumableModule consumableModule;

    private static Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    private long _updateInterval = Config.getInt("game.updateInterval");
    private long _lastUpdate;
    private int _index;

    private Map<String, String> _data = new HashMap<>();

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        _index = 0;
        renderer.draw(BG_COLOR, 0, 0, 2000, 2000);

        _data.entrySet().forEach(entry -> drawDebug(renderer, "UI", entry.getKey() + ": " + entry.getValue()));

        if (characterModule != null && characterModule.getCharacters() != null) {
            characterModule.getCharacters().forEach(character -> drawDebugCharacter(renderer, character));
        }

        if (itemModule != null && itemModule.getItems() != null) {
            itemModule.getItems().forEach(item -> drawDebugItem(renderer, item));
        }

        if (consumableModule != null && consumableModule.getConsumables() != null) {
            Map<ItemInfo, Integer> consumables = new HashMap<>();
            consumableModule.getConsumables().forEach(consumable -> {
                int quantity = consumables.containsKey(consumable.getInfo()) ? consumables.get(consumable.getInfo()) : 0;
                consumables.put(consumable.getInfo(), quantity + consumable.getQuantity());
            });

            consumables.entrySet().forEach(entry -> drawDebugConsumableInfo(renderer, entry.getKey(), entry.getValue()));
        }

        ApplicationClient.mainRenderer.getRenders()
                .forEach(render -> drawDebug(renderer, "Render", render.getClass().getSimpleName() + " " + render.getLevel()));
    }

    private void drawDebugItem(GDXRenderer renderer, UsableItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getName()).append(" ").append(item.getParcel().x).append("x").append(item.getParcel().y);

        if (item.getFactory() != null) {
            sb.append(" factory: ").append(item.getFactory().getMessage());
            if (item.getFactory().getRunningReceipt() != null) {
                sb.append(" cost remaining: ").append(item.getFactory().getRunningReceipt().getCostRemaining());
            }
        }

        if (item.getInventory() != null) {
            sb.append(" inventory: ");
            item.getInventory().forEach(consumable -> sb.append(consumable.getLabel()).append("x").append(consumable.getQuantity()).append(" "));
        }

        drawDebug(renderer, "Item", sb.toString());
    }

    private void drawDebugConsumableInfo(GDXRenderer renderer, ItemInfo itemInfo, int quantity) {
        StringBuilder sb = new StringBuilder();
        sb.append(itemInfo.label).append(" x ").append(quantity);

        drawDebug(renderer, "Consumable", sb.toString());
    }

    private void drawDebugCharacter(GDXRenderer renderer, CharacterModel character) {
        StringBuilder sb = new StringBuilder();
        sb.append(character.getName()).append(" ").append(character.getParcel().x).append("x").append(character.getParcel().y);
        if (character.getJob() != null) {
            sb.append(" job: ").append(character.getJob().getLabel());
        }
        drawDebug(renderer, "Character", sb.toString());
    }

    @Override
    public void onMouseMove(GameEvent event) {
        _data.put("Cursor screen position", event.mouseEvent.x + " x " + event.mouseEvent.y);
        _data.put("Cursor world position", ApplicationClient.mainRenderer.getViewport().getWorldPosX(event.mouseEvent.x) + " x " + ApplicationClient.mainRenderer.getViewport().getWorldPosY(event.mouseEvent.y));
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.F12) {
            toggleVisibility();
        }
    }

    private void drawDebug(GDXRenderer renderer, String label, Object object) {
        renderer.draw("[" + label.toUpperCase() + "] " + object, 18, 12, (_index * 20) + 12, Color.BLACK);
        renderer.draw("[" + label.toUpperCase() + "] " + object, 18, 11, (_index * 20) + 11, Color.BLACK);
        renderer.draw("[" + label.toUpperCase() + "] " + object, 18, 10, (_index * 20) + 10, Color.WHITE);
        _index++;
    }

    @Override
    public void onGameUpdate(Game game) {
        _lastUpdate = System.currentTimeMillis();
    }

    public int getLevel() {
        return 999;
    }
}