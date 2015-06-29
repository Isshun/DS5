package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.util.Constant;

import java.util.List;

public class JobRenderer extends BaseRenderer {
//	private RenderTexture 					_cache;
//	private HashMap<ActionType, ColorView> 		_rectangles;
	private int[][] 						_areas;

	public JobRenderer() {

		// TODO
//		try {
//			_cache = new RenderTexture();
//			_cache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
//			_cache.display();
//		} catch (TextureCreationException e) {
//			e.printStackTrace();
//		}

//		_rectangles = new HashMap<>();
//		addRectangle(ActionType.BUILD, JobManager.COLOR_BUILD);
//		addRectangle(ActionType.DESTROY, JobManager.COLOR_DESTROY);
//		addRectangle(ActionType.GATHER, JobManager.COLOR_GATHER);
//		addRectangle(ActionType.MINING, JobManager.COLOR_MINING);
//		addRectangle(ActionType.MOVE, JobManager.COLOR_MOVE);
//		addRectangle(ActionType.REFILL, JobManager.COLOR_REFILL);
//		addRectangle(ActionType.STORE, JobManager.COLOR_STORE);
//		addRectangle(ActionType.TAKE, JobManager.COLOR_TAKE);
//		addRectangle(ActionType.USE, JobManager.COLOR_USE);
//		addRectangle(ActionType.USE_INVENTORY, JobManager.COLOR_USE_INVENTORY);
//		addRectangle(ActionType.WORK, JobManager.COLOR_WORK);
	}


//	private void addRectangle(ActionType onAction, Color color) {
//		ColorView rectangle = ViewFactory.getInstance().createColorView(Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
//		rectangle.setBackgroundColor(new Color((int) (color.r * 1.2), (int) (color.g * 1.2), (int) (color.b * 1.2), 42));
//		_rectangles.put(onAction, rectangle);
//
//	}

	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		if (_areas == null) {
			_areas = new int[Game.getWorldManager().getWidth()][Game.getWorldManager().getHeight()];
		}

		int frame = MainRenderer.getFrame();
		List<BaseJobModel> jobs = JobManager.getInstance().getJobs();
		for (BaseJobModel job: jobs) {
			int x = job.getX();
			int y = job.getY();
//			if (_parcels[x][y] != frame && job.isFinish() == false) {
			if (job.isFinish() == false) {
                SpriteModel sprite = SpriteManager.getInstance().getIcon(job.getIcon());
                if (sprite != null) {
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    renderer.draw(sprite, effect);
                }
//				_parcels[x][y] = frame;
			}
		}
	}


	@Override
	public void onRefresh(int frame) {
		// TODO Auto-generated method stub

	}

}