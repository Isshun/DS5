package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.character.JobModule;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.util.Constant;

public class JobRenderer extends BaseRenderer {
	private int[][] 						_areas;

	public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
		if (_areas == null) {
			_areas = new int[Game.getInstance().getInfo().worldWidth][Game.getInstance().getInfo().worldHeight];
		}

		int offsetX = viewport.getPosX();
		int offsetY = viewport.getPosY();
        JobModule.getInstance().getJobs().stream().filter(job -> !job.isFinish()).forEach(job ->
                job.onDraw((x, y) -> renderer.draw(job.getIconDrawable(), offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT)));
	}


	@Override
	public void onRefresh(int frame) {
	}

	@Override
	public boolean isActive(GameConfig config) {
		return config.render.job;
	}

}