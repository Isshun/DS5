package org.smallbox.faraway.client.layer.ui;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.Game;

@GameObject
@GameLayer(level = 999, visible = true)
public class FPSLayer extends BaseLayer {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private Game game;

    private long lastMem;
    private long lastReset;
    private long memSinceReset;
    private long memBySecond;

    @OnGameUpdate
    private void onGameUpdate() {
        long footPrint = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        if (footPrint > 0) {
            if (lastMem < footPrint) {
                memSinceReset += footPrint - lastMem;
            }
            lastMem = footPrint;
        }

        if (lastReset + 1000 < System.currentTimeMillis()) {
            memBySecond = memBySecond > 0 ? (memBySecond + memSinceReset) / 2 : memSinceReset;
            memSinceReset = 0;
            lastReset = System.currentTimeMillis();
        }
    }

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

//        List<String> wmiClassesList = WMI4Java.get().listClasses();
//        List<String> wmiClassesList = WMI4Java.get().namespace("root/WMI").listClasses();
//        Map<String, String> wmiObjectProperties2 = WMI4Java.get().getWMIObject("win32_process");
//        Map<String, String> wmiObjectProperties = WMI4Java.get().getWMIObject("Win32_PerfFormattedData_PerfProc_Process");
        renderer.drawText(applicationConfig.getResolutionWidth() - 530, applicationConfig.getResolutionHeight() - 80 + 10, "Heap: " + (heapSize - heapFreeSize) / 1000 / 1000, Color.WHITE, 12);
        renderer.drawText(applicationConfig.getResolutionWidth() - 530, applicationConfig.getResolutionHeight() - 80 + 25, "/s " + memBySecond / 1000 / 1000 + " Mo", Color.WHITE, 12);
        renderer.drawText(applicationConfig.getResolutionWidth() - 530, applicationConfig.getResolutionHeight() - 80 + 40, "F " + frame, Color.WHITE, 12);
        renderer.drawRectangle(applicationConfig.getResolutionWidth() - 530 + frame / 5 % 32, applicationConfig.getResolutionHeight() - 80 + 55, 2, 2, Color.WHITE);
    }

}