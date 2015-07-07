package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.util.Constant;

public class JobRenderer extends BaseRenderer {
	private int[][] 						_areas;

	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		if (_areas == null) {
			_areas = new int[Game.getWorldManager().getWidth()][Game.getWorldManager().getHeight()];
		}

		int offsetX = effect.getViewport().getPosX();
		int offsetY = effect.getViewport().getPosY();
		for (BaseJobModel job : JobManager.getInstance().getJobs()) {
			int x = job.getX();
			int y = job.getY();
			if (!job.isFinish()) {
				SpriteModel sprite = SpriteManager.getInstance().getIcon(job.getIcon());
				if (sprite != null) {
					renderer.draw(sprite, offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT);
				}
			}
		}
	}


	@Override
	public void onRefresh(int frame) {
		// TODO Auto-generated method stub

	}

}