package alone.in.deepspace.engine.renderer;

import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.util.Constant;

public class JobRenderer implements IRenderer {
	private RenderTexture 	_cache;

	public JobRenderer() {
		try {
			_cache = new RenderTexture();
			_cache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
			_cache.display();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
	}


	public void onDraw(RenderWindow app, RenderStates render, double animProgress) {
//		RectangleShape rectangleItem = new RectangleShape(new Vector2f(Constant.TILE_WIDTH, Constant.TILE_HEIGHT));
//
//		List<Job> jobs = JobManager.getInstance().getJobs();
//		for (Job job: jobs) {
//			if (job.getAction() != Action.USE || job.isActive()) {
//				Color color = job.getColor();
//				rectangleItem.setFillColor(new Color(color.r, color.g, color.b, 100));
//				rectangleItem.setPosition(new Vector2f(job.getX() * Constant.TILE_WIDTH, job.getY() * Constant.TILE_HEIGHT));
//				app.draw(rectangleItem, render);
//			}
//		}
	}

}