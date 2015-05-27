package org.smallbox.faraway.renderer;

import java.util.HashMap;
import java.util.List;

import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.JobManager.Action;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.engine.util.Constant;

public class JobRenderer implements IRenderer {
	private RenderTexture 					_cache;
	private HashMap<Action, ColorView> 		_rectangles;
	private int[][] 						_areas;

	public JobRenderer() {
		_areas = new int[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
		
		try {
			_cache = new RenderTexture();
			_cache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
			_cache.display();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		
		_rectangles = new HashMap<>();
		addRectangle(Action.BUILD, JobManager.COLOR_BUILD);
		addRectangle(Action.DESTROY, JobManager.COLOR_DESTROY);
		addRectangle(Action.GATHER, JobManager.COLOR_GATHER);
		addRectangle(Action.MINING, JobManager.COLOR_MINING);
		addRectangle(Action.MOVE, JobManager.COLOR_MOVE);
		addRectangle(Action.REFILL, JobManager.COLOR_REFILL);
		addRectangle(Action.STORE, JobManager.COLOR_STORE);
		addRectangle(Action.TAKE, JobManager.COLOR_TAKE);
		addRectangle(Action.USE, JobManager.COLOR_USE);
		addRectangle(Action.USE_INVENTORY, JobManager.COLOR_USE_INVENTORY);
		addRectangle(Action.WORK, JobManager.COLOR_WORK);
	}


	private void addRectangle(Action action, Color color) {
		ColorView rectangle = new ColorView(Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
		rectangle.setBackgroundColor(new Color((int) (color.r * 1.2), (int) (color.g * 1.2), (int) (color.b * 1.2), 42));
		_rectangles.put(action, rectangle);

	}


	public void onDraw(Renderer renderer, RenderEffect effect, double animProgress) {
		int frame = MainRenderer.getFrame();
		List<Job> jobs = JobManager.getInstance().getJobs();
		for (Job job: jobs) {
			int x = job.getX();
			int y = job.getY();
			if (_areas[x][y] != frame && job.isFinish() == false) {
				ColorView rectangle = _rectangles.get(job.getAction());
				if (rectangle != null) {
					rectangle.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
					renderer.draw(rectangle, effect);
				}
				_areas[x][y] = frame;
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