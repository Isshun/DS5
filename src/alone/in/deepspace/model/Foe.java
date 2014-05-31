package alone.in.deepspace.model;

import java.util.Vector;
import alone.in.deepspace.model.job.Job;

public class Foe extends Movable {

	public Foe(int id, int x, int y) {
		super(id, x, y);
	}

	@Override
	public void onPathComplete(Vector<Position> path, Job item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPathFailed(Job item) {
		// TODO Auto-generated method stub
		
	}

}
