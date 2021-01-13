package org.smallbox.faraway.client.render.layer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.GpuMemUtils;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

@GameObject
@GameLayer(level = 999, visible = true)
public class FPSLayer extends BaseLayer {

    @Inject
    private Game game;

    @Inject
    private GpuMemUtils gpuMemUtils;


    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

//        List<String> wmiClassesList = WMI4Java.get().listClasses();
//        List<String> wmiClassesList = WMI4Java.get().namespace("root/WMI").listClasses();
//        Map<String, String> wmiObjectProperties2 = WMI4Java.get().getWMIObject("win32_process");
//        Map<String, String> wmiObjectProperties = WMI4Java.get().getWMIObject("Win32_PerfFormattedData_PerfProc_Process");
        renderer.drawTextUI(10, 10, 12, Color.RED, "Heap: " + (heapSize - heapFreeSize) / 1000 / 1000);
        renderer.drawTextUI(10, 25, 12, Color.RED, "Heap: " + Gdx.app.getJavaHeap());
        renderer.drawTextUI(10, 40, 12, Color.RED, "Native: " + Gdx.app.getNativeHeap());
        renderer.drawTextUI(10, 55, 12, Color.RED, "T " + game.getTick());
        renderer.drawTextUI(10, 70, 12, Color.RED, "F " + frame);
        renderer.drawPixelUI(10 + frame / 5 % 32, 55, 2, 2, Color.RED);
    }

}