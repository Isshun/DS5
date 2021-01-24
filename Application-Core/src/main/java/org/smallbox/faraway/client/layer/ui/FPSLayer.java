package org.smallbox.faraway.client.layer.ui;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.GpuMemUtils;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.config.ApplicationConfig;

@GameObject
@GameLayer(level = 999, visible = true)
public class FPSLayer extends BaseLayer {
    @Inject private Game game;
    @Inject private GpuMemUtils gpuMemUtils;
    @Inject private ApplicationConfig applicationConfig;


    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

//        List<String> wmiClassesList = WMI4Java.get().listClasses();
//        List<String> wmiClassesList = WMI4Java.get().namespace("root/WMI").listClasses();
//        Map<String, String> wmiObjectProperties2 = WMI4Java.get().getWMIObject("win32_process");
//        Map<String, String> wmiObjectProperties = WMI4Java.get().getWMIObject("Win32_PerfFormattedData_PerfProc_Process");
        renderer.drawText(applicationConfig.getResolutionWidth() - 530, applicationConfig.getResolutionHeight() - 80 + 10, "Heap: " + (heapSize - heapFreeSize) / 1000 / 1000, Color.WHITE, 12);
        renderer.drawText(applicationConfig.getResolutionWidth() - 530, applicationConfig.getResolutionHeight() - 80 + 25, "T " + game.getTick(), Color.WHITE, 12);
        renderer.drawText(applicationConfig.getResolutionWidth() - 530, applicationConfig.getResolutionHeight() - 80 + 40, "F " + frame, Color.WHITE, 12);
        renderer.drawRectangle(applicationConfig.getResolutionWidth() - 530 + frame / 5 % 32, applicationConfig.getResolutionHeight() - 80 + 55, 2, 2, Color.WHITE);
    }

}