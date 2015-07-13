package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.util.Constant;

public class JobRenderer extends BaseRenderer {
	private int[][] 						_areas;

	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		if (_areas == null) {
			_areas = new int[Game.getWorldManager().getWidth()][Game.getWorldManager().getHeight()];
		}

		int offsetX = effect.getViewport().getPosX();
		int offsetY = effect.getViewport().getPosY();
        JobManager.getInstance().getJobs().stream().filter(job -> !job.isFinish()).forEach(job ->
                job.onDraw((x, y) -> renderer.drawIcon(job.getIcon(), offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT)));
	}


	@Override
	public void onRefresh(int frame) {
	}

}