package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.job.BaseJob;

import java.util.List;

public class JobRenderer implements IRenderer {
//	private RenderTexture 					_cache;
//	private HashMap<ActionType, ColorView> 		_rectangles;
	private int[][] 						_areas;

	public JobRenderer() {
		_areas = new int[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];

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


//	private void addRectangle(ActionType action, Color color) {
//		ColorView rectangle = ViewFactory.getInstance().createColorView(Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
//		rectangle.setBackgroundColor(new Color((int) (color.r * 1.2), (int) (color.g * 1.2), (int) (color.b * 1.2), 42));
//		_rectangles.put(action, rectangle);
//
//	}

	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		int frame = MainRenderer.getFrame();
		List<BaseJob> jobs = JobManager.getInstance().getJobs();
		for (BaseJob job: jobs) {
			int x = job.getX();
			int y = job.getY();
//			if (_areas[x][y] != frame && job.isFinish() == false) {
			if (job.isFinish() == false) {
                SpriteModel sprite = job.getIcon();
                if (sprite != null) {
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    renderer.draw(sprite, effect);
                }
//				_areas[x][y] = frame;
			}
		}
	}


	@Override
	public void onRefresh(int frame) {
		// TODO Auto-generated method stub

	}


	@Override
	public void invalidate(int x, int y) {
		// TODO Auto-generated method stub

	}


	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

}