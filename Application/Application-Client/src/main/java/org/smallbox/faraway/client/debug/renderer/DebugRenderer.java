package org.smallbox.faraway.client.debug.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.dependencyInjector.Component;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.HashMap;
import java.util.Map;

@Component
@GameRenderer(level = 999, visible = false)
public class DebugRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private ItemModule itemModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private PlantModule plantModule;

    @BindModule
    private ConsumableModule consumableModule;

    private static Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    private long _lastUpdate;
    private int _index;

    private Map<String, String> _data = new HashMap<>();

    private enum Mode { CONSUMABLE, ITEM, PLANT, RENDER, OTHER, CHARACTER, JOB }

    private Mode _mode = Mode.OTHER;

    {
        _data.put("Cursor screen position", "");
        _data.put("Cursor world position", "");
    }

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _index = 0;
        renderer.drawPixel(0, 0, 2000, 2000, BG_COLOR);

        switch (_mode) {

            // Display consumables
            case CONSUMABLE:
                if (consumableModule != null && consumableModule.getConsumables() != null) {
                    Map<ItemInfo, Integer> consumables = new HashMap<>();
                    consumableModule.getConsumables().forEach(consumable -> {
                        int quantity = consumables.containsKey(consumable.getInfo()) ? consumables.get(consumable.getInfo()) : 0;
                        consumables.put(consumable.getInfo(), quantity + consumable.getFreeQuantity());
                    });

                    consumables.entrySet().forEach(entry -> drawDebugConsumableInfo(renderer, entry.getKey(), entry.getValue()));
                }
                break;

            // Display items
            case ITEM:
                if (itemModule != null && itemModule.getItems() != null) {
                    itemModule.getItems().forEach(item -> drawDebugItem(renderer, item));
                }
                break;

            case PLANT:
                if (plantModule != null && plantModule.getPlants() != null) {
                    plantModule.getPlants().forEach(plant -> {
                        drawDebug(renderer, "Plant", plant.getLabel() + " " + plant.getMaturity());
                    });
                }
                break;

            // Display renders
            case RENDER:
                if (ApplicationClient.mainRenderer != null) {
                    ApplicationClient.mainRenderer.getRenders()
                            .forEach(render -> drawDebug(renderer, "Render", "[" + (render.isVisible() ? "X" : " ") + "] " + render.getClass().getSimpleName() + " (" + render.getLevel() + ")"));
                }
                break;

            // Display jobs
            case JOB:
                if (jobModule != null && jobModule.getJobs() != null) {
                    jobModule.getJobs().forEach(job -> {
                        drawDebug(renderer, "JOB", job.getMainLabel() + " -> " + job.getLabel() + " -> " + job.getProgress() + " -> " + job.getLastReturn());
                        job.getTasks().forEach(task -> drawDebug(renderer, "JOB", "  - " + task.label));
                    });
                }
                break;

            // Display characters
            case CHARACTER:
                if (characterModule != null && characterModule.getCharacters() != null) {
                    characterModule.getCharacters().forEach(character -> drawDebugCharacter(renderer, character));
                }
                break;

            case OTHER:
                drawDebug(renderer, "VIEWPORT", "Floor: " + ApplicationClient.mainRenderer.getViewport().getFloor());
                drawDebug(renderer, "VIEWPORT", "Size: " + ApplicationClient.mainRenderer.getViewport().getWidth() + " x " + ApplicationClient.mainRenderer.getViewport().getHeight());

                drawDebug(renderer, "WORLD", "Size: " + Application.gameManager.getGame().getInfo().worldWidth + " x " + Application.gameManager.getGame().getInfo().worldHeight + " x " + Application.gameManager.getGame().getInfo().worldFloors);
                drawDebug(renderer, "WORLD", "Ground floor: " + Application.gameManager.getGame().getInfo().groundFloor);

                _data.entrySet().forEach(entry -> drawDebug(renderer, "UI", entry.getKey() + ": " + entry.getValue()));

                ApplicationClient.shortcutManager.getBindings().forEach(strategy -> drawDebug(renderer, "SHORTCUT", strategy.label + " -> " + strategy.key));
                break;
        }

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
            item.getInventory().forEach(consumable -> sb.append(consumable.getLabel()).append("x").append(consumable.getFreeQuantity()).append(" "));
        }

        drawDebug(renderer, "Item", sb.toString());
    }

    private void drawDebugConsumableInfo(GDXRenderer renderer, ItemInfo itemInfo, int quantity) {
        drawDebug(renderer, "Consumable", itemInfo.label + " x " + quantity);
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

    private void drawDebug(GDXRenderer renderer, String label, Object object) {
        renderer.drawText(12, (_index * 20) + 12, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawText(11, (_index * 20) + 11, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawText(10, (_index * 20) + 10, 18, Color.WHITE, "[" + label.toUpperCase() + "] " + object);
        _index++;
    }

    @Override
    public void onGameUpdate(Game game) {
        _lastUpdate = System.currentTimeMillis();
    }

    @GameShortcut(key = GameEventListener.Key.F12)
    public void onToggleVisibility() {
        toggleVisibility();
    }

    @GameShortcut(key = GameEventListener.Key.D_1)
    public void onD1() {
        _mode = Mode.OTHER;
    }

    @GameShortcut(key = GameEventListener.Key.D_2)
    public void onD2() {
        _mode = Mode.CONSUMABLE;
    }

    @GameShortcut(key = GameEventListener.Key.D_3)
    public void onD3() {
        _mode = Mode.ITEM;
    }

    @GameShortcut(key = GameEventListener.Key.D_4)
    public void onD4() {
        _mode = Mode.PLANT;
    }

    @GameShortcut(key = GameEventListener.Key.D_5)
    public void onD5() {
        _mode = Mode.RENDER;
    }

    @GameShortcut(key = GameEventListener.Key.D_6)
    public void onD6() {
        _mode = Mode.CHARACTER;
    }

    @GameShortcut(key = GameEventListener.Key.D_7)
    public void onD7() {
        _mode = Mode.JOB;
    }

}