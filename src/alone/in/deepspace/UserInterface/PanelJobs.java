package alone.in.DeepSpace.UserInterface;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Managers.JobManager;
import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.Job;
import alone.in.DeepSpace.Utils.Constant;

public class PanelJobs extends UserSubInterface {
	private static final int 	FRAME_WIDTH = 380;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public PanelJobs(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(0, 0, 0, 180));
	}
	
	@Override
	public void onRefresh() {
		if (_isVisible == false) {
			return;
		}
		
		  int posX = 20;
		  int posY = 20;

		  Text text = new Text();
		  text.setFont(SpriteManager.getInstance().getFont());
		  text.setCharacterSize(12);

		  // Display jobs
		  List<Job> jobs = JobManager.getInstance().getJobs();

		  text.setColor(Color.WHITE);
		  text.setString("Operation");
		  text.setCharacterSize(28);
		  text.setPosition(posX, posY);
		  _app.draw(text, _render);
		  text.setColor(Color.YELLOW);
		  text.setStyle(Text.UNDERLINED);
		  text.setString("O");
		  _app.draw(text, _render);

		  text.setStyle(Text.REGULAR);
		  text.setColor(Color.WHITE);
		  text.setCharacterSize(16);
		  text.setString("jobs: " + jobs.size());
		  text.setPosition(posX, posY + 38);
		  _app.draw(text, _render);
		  
		  text.setCharacterSize(12);
		  int i = 0;
		  for (Job job: jobs) {
			if (i < 50) {
			  String oss = "Job # " + job.getId()
				  + ": " + JobManager.getActionName(job.getAction())
				  + " " + BaseItem.getItemName(job.getItemType());
			  if (job.getCharacter() != null) {
				  switch (job.getAction()) {
				  case BUILD: text.setColor(Color.YELLOW); break;
				  case MOVE: text.setColor(Color.CYAN); break;
				  case GATHER: text.setColor(Color.GREEN); break;
				  case NONE: text.setColor(Color.BLACK); break;
				  case USE: text.setColor(Color.BLUE); break;
				  }
				oss += " (" + job.getCharacter().getName() + ")";
			  } else {
				text.setColor(Color.RED);
				oss += " (on queue)";
			  }
			  text.setString(oss);
			  text.setPosition(posX + Constant.UI_PADDING, posY + 52 + Constant.UI_PADDING + (14 * i++));
			  _app.draw(text, _render);
			}
		  }

	}
}
