package alone.in.deepspace.Models;

import java.util.Vector;

public interface PathManagerCallback {
//	  void	onPathSearch(Vector<Position> path, Job item);
	  void	onPathComplete(Vector<Position> path, Job item);
	  void	onPathFailed(Job item);
}
