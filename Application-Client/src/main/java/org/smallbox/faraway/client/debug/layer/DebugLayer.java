package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.GameEventManager;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.module.TaskClientModule;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.PlantModule;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@GameObject
@ApplicationObject
@GameLayer(level = 999, visible = false)
public class DebugLayer extends BaseLayer {

    @Inject
    private Game game;

    @Inject
    private ShortcutManager shortcutManager;

    @Inject
    private SelectionManager selectionManager;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private ItemModule itemModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private PlantModule plantModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private GameEventManager gameEventManager;

    @Inject
    private TaskClientModule taskClientModule;

    @Inject
    private LayerManager layerManager;

    @Inject
    private UIManager uiManager;

    private static Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);

    private long _lastUpdate;
    private int _index;

    private Map<String, String> _data = new HashMap<>();

    private enum Mode {TASKS, UI, CONSUMABLE, ITEM, PLANT, LAYER, SHORTCUTS, CHARACTER, JOB, MODULE}

    private Mode _mode = Mode.TASKS;

    {
        _data.put("Cursor screen position", "");
        _data.put("Cursor world position", "");
        _data.put("Parcel isWalkable", "");
    }

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _index = 2;
        renderer.drawPixel(0, 0, 2000, 2000, BG_COLOR);

        drawHeaders(renderer);

        switch (_mode) {

            // Events
            case TASKS:
                if (taskClientModule != null && taskClientModule.getTasks() != null) {
                    taskClientModule.getTasks().forEach(task -> drawDebug(renderer, "Task ", task.id + " " + task.label + " " + task.elapsed + " " + task.duration ));
                }
                break;

            // Display consumables
            case CONSUMABLE:
                if (consumableModule != null && consumableModule.getConsumables() != null) {
                    Map<ItemInfo, Integer> consumables = new HashMap<>();
                    consumableModule.getConsumables().forEach(consumable -> {
                        int quantity = consumables.getOrDefault(consumable.getInfo(), 0);
                        consumables.put(consumable.getInfo(), quantity + consumable.getFreeQuantity());
                    });

                    consumables.forEach((key, value) -> drawDebugConsumableInfo(renderer, key, value));
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
                    plantModule.getPlants().forEach(plant -> drawDebug(renderer, "Plant", plant.getLabel() + " " + plant.getMaturity()));
                }
                break;

            // Display renders
            case LAYER:
                if (layerManager != null) {
                    layerManager.getLayers().stream()
                            .sorted((o1, o2) -> (int)(o2.getCumulateTime() - o1.getCumulateTime()))
                            .forEach(render -> drawDebug(renderer, "Render",
                                    String.format("%-32s visible: %-5s total: %-5d med: %.2f",
                                            render.getClass().getSimpleName(),
                                            render.isVisible() ? "x" : " ",
                                            render.getCumulateTime() / 1000,
                                            render.getCumulateTime() / 1000 / (double)game.getTick())));
                }
                break;

            // Display jobs
            case JOB:
                if (jobModule != null && jobModule.getJobs() != null) {
                    jobModule.getJobs().forEach(job -> {
                        drawDebug(renderer, "JOB",
                                String.format("%s, %.2f, %s",
                                        job.getMainLabel(),
                                        job.getProgress(),
                                        job.getLastReturn()
                                )
                        );
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

            // Display characters
            case MODULE:
                game.getModules().stream()
                        .sorted(Comparator.comparingLong(AbsGameModule::getCumulateTime).reversed())
                        .forEach(module -> drawDebug(renderer, "MODULE",
                                String.format("%-32s total: %-5d med: %.2f",
                                        module.getName(),
                                        module.getCumulateTime() / 1000,
                                        module.getCumulateTime() / 1000 / (double)game.getTick())));
                break;

            case SHORTCUTS:
                shortcutManager.getBindings().forEach(strategy -> drawDebug(renderer, "SHORTCUT", strategy.label.replace("org.smallbox.faraway.", "") + " -> " + strategy.key));
                break;

            case UI:
                long heapSize = Runtime.getRuntime().totalMemory();
                long heapFreeSize = Runtime.getRuntime().freeMemory();
                drawDebug(renderer, "VIEWPORT", "Heap: " + ((heapSize - heapFreeSize) / 1000 / 1000) + "mb");

                drawDebug(renderer, "VIEWPORT", "Floor: " + layerManager.getViewport().getFloor());
                drawDebug(renderer, "VIEWPORT", "Size: " + layerManager.getViewport().getWidth() + " x " + layerManager.getViewport().getHeight());

                drawDebug(renderer, "WORLD", "Size: " + game.getInfo().worldWidth + " x " + game.getInfo().worldHeight + " x " + game.getInfo().worldFloors);
                drawDebug(renderer, "WORLD", "Ground floor: " + game.getInfo().groundFloor);

                if (selectionManager.getSelected() != null) {
                    selectionManager.getSelected().forEach(selected -> drawDebug(renderer, "SELECTION", "Current: " + selected));
                }

                drawDebug(renderer, "Cursor screen position", gameEventManager.getMouseX() + " x " + gameEventManager.getMouseY());
                drawDebug(renderer, "Cursor world position", layerManager.getViewport().getWorldPosX(gameEventManager.getMouseX()) + " x " + layerManager.getViewport().getWorldPosY(gameEventManager.getMouseY()));

                ParcelModel parcel = WorldHelper.getParcel(
                        layerManager.getViewport().getWorldPosX(gameEventManager.getMouseX()),
                        layerManager.getViewport().getWorldPosY(gameEventManager.getMouseY()),
                        layerManager.getViewport().getFloor());
                drawDebug(renderer, "Parcel isWalkable", parcel != null ? String.valueOf(parcel.isWalkable()) : "no parcel");
                drawDebug(renderer, "###############################", "");

                uiManager.getRootViews().forEach(rootView -> {
                    drawDebug(renderer, rootView.getView().getPath(), rootView.getView().isVisible() ? "visible" : "");
                });
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

    private void drawHeaders(GDXRenderer renderer) {
        int index = 0;
        int offset = 0;
        for (Mode mode: Mode.values()) {
            renderer.drawText((offset * 10) + 12, 12, 18, Color.BLACK, (index + 1) + ") [" + mode.name().toUpperCase() + "] ");
            renderer.drawText((offset * 10) + 11, 11, 18, Color.BLACK, (index + 1) + ") [" + mode.name().toUpperCase() + "] ");
            renderer.drawText((offset * 10) + 10, 10, 18, _mode == mode ? Color.CORAL : Color.WHITE, (index + 1) + ") [" + mode.name().toUpperCase() + "] ");
            offset += mode.name().length() + 8;
            index++;
        }
    }

    private void drawDebug(GDXRenderer renderer, String label, Object object) {
        renderer.drawText(12, (_index * 20) + 12, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawText(11, (_index * 20) + 11, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawText(10, (_index * 20) + 10, 18, Color.WHITE, "[" + label.toUpperCase() + "] " + object);
        _index++;
    }

    private void setModeIndex(int index) {
        if (index - 1 < Mode.values().length) {
            _mode = Mode.values()[index - 1];
        }
    }

    @Override
    public void onGameUpdate(Game game) {
        _lastUpdate = System.currentTimeMillis();
    }

    @GameShortcut(key = Input.Keys.F12)
    public void onToggleVisibility() {
        toggleVisibility();
    }

    @GameShortcut(key = Input.Keys.NUM_1)
    public void onD1() { setModeIndex(1); }

    @GameShortcut(key = Input.Keys.NUM_2)
    public void onD2() { setModeIndex(2); }

    @GameShortcut(key = Input.Keys.NUM_3)
    public void onD3() { setModeIndex(3); }

    @GameShortcut(key = Input.Keys.NUM_4)
    public void onD4() { setModeIndex(4); }

    @GameShortcut(key = Input.Keys.NUM_5)
    public void onD5() { setModeIndex(5); }

    @GameShortcut(key = Input.Keys.NUM_6)
    public void onD6() { setModeIndex(6); }

    @GameShortcut(key = Input.Keys.NUM_7)
    public void onD7() { setModeIndex(7); }

    @GameShortcut(key = Input.Keys.NUM_8)
    public void onD8() { setModeIndex(8); }

    @GameShortcut(key = Input.Keys.NUM_9)
    public void onD9() { setModeIndex(9); }

    @GameShortcut(key = Input.Keys.NUM_0)
    public void onD0() { setModeIndex(10); }

}