package org.smallbox.faraway.model;

import org.newdawn.slick.util.pathfinding.Path;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.job.BaseJob;

public class Foe extends Movable {

	public Foe(int x, int y) {
		super(Utils.getUUID(), x, y);
	}

	@Override
	public void onPathComplete(Path path, BaseJob item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPathFailed(BaseJob item) {
		// TODO Auto-generated method stub
		
	}

}