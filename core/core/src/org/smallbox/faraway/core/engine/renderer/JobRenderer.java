package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.util.Constant;

public class JobRenderer extends BaseRenderer {
    private int[][]         _areas;
    private int             _floor;

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_areas == null) {
            _areas = new int[Game.getInstance().getInfo().worldWidth][Game.getInstance().getInfo().worldHeight];
        }

        int offsetX = viewport.getPosX();
        int offsetY = viewport.getPosY();
        int floor = WorldHelper.getCurrentFloor();
        ModuleHelper.getJobModule().getJobs().stream().filter(job -> !job.isFinish()).forEach(job ->
                job.draw((x, y, z) -> {
                    if (floor == z)
                        renderer.draw(job.getIconDrawable(), offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT);
                }));
    }


    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.job;
    }


    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}