package alone.in.deepspace.model;

import org.newdawn.slick.util.pathfinding.Path;

import alone.in.deepspace.model.job.Job;

public class Foe extends Movable {

	public Foe(int id, int x, int y) {
		super(id, x, y);
	}

	@Override
	public void onPathComplete(Path path, Job item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPathFailed(Job item) {
		// TODO Auto-generated method stub
		
	}

}
