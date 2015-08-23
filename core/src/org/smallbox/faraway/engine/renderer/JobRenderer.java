package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.character.JobManager;
import org.smallbox.faraway.util.Constant;

public class JobRenderer extends BaseRenderer {
	private int[][] 						_areas;

	public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
		if (_areas == null) {
			_areas = new int[Game.getWorldManager().getWidth()][Game.getWorldManager().getHeight()];
		}

		int offsetX = viewport.getPosX();
		int offsetY = viewport.getPosY();
        JobManager.getInstance().getJobs().stream().filter(job -> !job.isFinish()).forEach(job ->
                job.onDraw((x, y) -> renderer.draw(job.getIconDrawable(), offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT)));
	}


	@Override
	public void onRefresh(int frame) {
	}

}