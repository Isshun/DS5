package org.smallbox.faraway.client.debug.dashboard;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.debug.dashboard.content.*;
import org.smallbox.faraway.client.debug.layer.*;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;

import java.util.*;
import java.util.function.BiConsumer;

@GameObject
@GameLayer(level = 999, visible = false)
public class DashboardLayer extends BaseLayer {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private ShortcutDashboardLayer shortcutDashboardLayer;
    @Inject private CharacterDashboardLayer characterDashboardLayer;
    @Inject private ConsoleDashboardLayer consoleDashboardLayer;
    @Inject private ConsumableDashboardLayer consumableDashboardLayer;
    @Inject private TaskDashboardLayer taskDashboardLayer;
    @Inject private ItemDashboardLayer itemDashboardLayer;
    @Inject private PlantDashboardLayer plantDashboardLayer;
    @Inject private ModuleDashboardLayer moduleDashboardLayer;
    @Inject private LayerDashboardLayer layerDashboardLayer;
    @Inject private JobDashboardLayer jobDashboardLayer;
    @Inject private TextureAssetsDashboardLayer textureAssetsDashboardLayer;
    @Inject private PixmapAssetsDashboardLayer pixmapAssetsDashboardLayer;
    @Inject private InterfaceDashboardLayer interfaceDashboardLayer;
    @Inject private DebugPathLayer debugPathLayer;
    @Inject private DebugCharacterLayer debugCharacterLayer;
    @Inject private DebugConsumableLayer debugConsumableLayer;
    @Inject private DebugGroundLayer debugGroundLayer;
    @Inject private DebugItemLayer debugItemLayer;
    @Inject private DebugViewLayer debugViewLayer;

    private static class DashboardLayerButton {
        public String label;
        public BaseLayer layer;

        public DashboardLayerButton(String label, BaseLayer layer) {
            this.label = label;
            this.layer = layer;
        }
    }

    private static final Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);
    private static final Color BUTTON_BG_COLOR = new Color(0f, 0.5f, 0.7f, 0.5f);

    private long _lastUpdate;

    private final Map<String, String> _data = new HashMap<>();
    private DashboardMode dashboardMode = DashboardMode.CONSOLE;
    private List<BiConsumer<Integer, Integer>> buttons;
    private List<DashboardLayerButton> debugLayers;

    {
        _data.put("Cursor screen position", "");
        _data.put("Cursor world position", "");
        _data.put("Parcel isWalkable", "");
    }

    @OnInit
    public void init() {
        buttons = new ArrayList<>();
        debugLayers = List.of(
                new DashboardLayerButton("Path", debugPathLayer),
                new DashboardLayerButton("Character", debugCharacterLayer),
                new DashboardLayerButton("Consumable", debugConsumableLayer),
                new DashboardLayerButton("Ground", debugGroundLayer),
                new DashboardLayerButton("Item", debugItemLayer),
                new DashboardLayerButton("View", debugViewLayer)
        );

        int offset = 10;
        for (DashboardMode dashboardMode : DashboardMode.values()) {
            int width = dashboardMode.name().length() * 10 + 60;
            int finalOffset = offset;
            buttons.add((x, y) -> {
                if (y >= 5 && y < 35 && x >= finalOffset && x < finalOffset + width) {
                    DashboardLayer.this.dashboardMode = dashboardMode;
                }
            });
            offset += width;
        }

        offset = 10;
        for (DashboardLayerButton button : debugLayers) {
            int width = button.label.length() * 10 + 60;
            int finalOffset = offset;
            buttons.add((x, y) -> {
                if (y >= 50 && y < 85 && x >= finalOffset && x < finalOffset + width - 10) {
                    button.layer.toggleVisibility();
                }
            });
            offset += width;
        }
    }

    @Override
    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (isVisible()) {
            drawHeaders(renderer);
            drawDebugLayer(renderer);
            Optional.ofNullable(currentDashboard()).ifPresent(dashboardLayer -> dashboardLayer.draw(renderer, frame));
        }
    }

    private DashboardLayerBase currentDashboard() {
        switch (dashboardMode) {
            case CONSOLE: return consoleDashboardLayer;
            case TASKS: return taskDashboardLayer;
            case CONSUMABLE: return consumableDashboardLayer;
            case ITEM: return itemDashboardLayer;
            case PLANT: return plantDashboardLayer;
            case LAYER: return layerDashboardLayer;
            case JOB: return jobDashboardLayer;
            case CHARACTER: return characterDashboardLayer;
            case MODULE: return moduleDashboardLayer;
            case SHORTCUTS: return shortcutDashboardLayer;
            case UI: return interfaceDashboardLayer;
            case TEXTURE: return textureAssetsDashboardLayer;
            case PIXMAP: return pixmapAssetsDashboardLayer;
        }
        return null;
    }

    private void drawDebugLayer(BaseRenderer renderer) {
        int offset = 0;
        for (DashboardLayerButton button : debugLayers) {
            drawButton(renderer, (offset * 10) + 4, 54, " [" + button.label.toUpperCase() + "] ", button.layer.isVisible());
            offset += button.label.length() + 6;
        }
    }

    private void drawHeaders(BaseRenderer renderer) {
        int offset = 0;
        for (DashboardMode dashboardMode : DashboardMode.values()) {
            drawButton(renderer, (offset * 10) + 4, 4, " [" + dashboardMode.name().toUpperCase() + "] ", this.dashboardMode == dashboardMode);
            offset += dashboardMode.name().length() + 6;
        }
    }

    private void drawButton(BaseRenderer renderer, int x, int y, String label, boolean isActive) {
        renderer.drawRectangle(x, y, label.length() * 10, 30, BUTTON_BG_COLOR, true);
        renderer.drawText(x + 2, y + 5, label, Color.BLACK, 18);
        renderer.drawText(x + 1, y + 6, label, Color.BLACK, 18);
        renderer.drawText(x, y + 7, label, isActive ? Color.CORAL : Color.WHITE, 18);
    }

    @Override
    public void onGameUpdate(Game game) {
        _lastUpdate = System.currentTimeMillis();
    }

    public void pageUp() {
        Optional.ofNullable(currentDashboard()).ifPresent(DashboardLayerBase::pageUp);
    }

    public void pageDown() {
        Optional.ofNullable(currentDashboard()).ifPresent(DashboardLayerBase::pageDown);
    }

    public void setMode(DashboardMode dashboardMode) {
        this.dashboardMode = dashboardMode;
    }

    public void click(int x, int y) {
        buttons.forEach(consumer -> consumer.accept(x, y));
    }
}