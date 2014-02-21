package alone.in.deepspace.Character;

import java.util.Vector;

import alone.in.deepspace.Models.Job;
import alone.in.deepspace.Models.Position;

public interface PathManagerCallback {
//	  void	onPathSearch(Vector<Position> path, Job item);
	  void	onPathComplete(Vector<Position> path, Job item);
	  void	onPathFailed(Job item);
}
