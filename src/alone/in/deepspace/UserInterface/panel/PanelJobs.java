package alone.in.deepspace.UserInterface.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.Job;

public class PanelJobs extends UserSubInterface {
	private static final Color 	COLOR_BUILD = new Color(170, 128, 64);
	private static final Color 	COLOR_BLOCKED = new Color(255, 20, 20);
	private static final Color 	COLOR_DESTROY = new Color(200, 20, 20);
	private static final Color 	COLOR_STORE = new Color(180, 100, 255);
	private static final Color 	COLOR_QUEUE = new Color(255, 255, 20);
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public PanelJobs(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(0, 0, 0, 180));
	}
	
	@Override
	public void onRefresh(RenderWindow app) {
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
		  app.draw(text, _render);
		  text.setColor(Color.YELLOW);
		  text.setStyle(Text.UNDERLINED);
		  text.setString("O");
		  app.draw(text, _render);

		  text.setStyle(Text.REGULAR);
		  text.setColor(Color.WHITE);
		  text.setCharacterSize(16);
		  text.setString("jobs: " + jobs.size());
		  text.setPosition(posX, posY + 38);
		  app.draw(text, _render);
		  
		  text.setCharacterSize(12);
		  int i = 0;
		  for (Job job: jobs) {
			if (i < 75) {
			  String oss = (job.getId()  < 10 ? "#0" : "#") + job.getId()
				  + " - " + JobManager.getActionName(job.getAction());
				  if (job.getItem() != null) {
					  oss += " " + job.getItem().getLabel();
				  }
			  if (job.getCharacter() != null) {
				  text.setColor(job.getColor());
				oss += " (" + job.getCharacter().getName() + ")";
			  } else if (job.getFail() > 0) {
				  switch (job.getReason()) {
				  case BLOCKED:
					  text.setColor(COLOR_BLOCKED);
					  oss += " (blocked: #" + job.getBlocked() + ")";
					  break;
				  case INTERRUPTE:
					  oss += " (interrupte)";
				  case NO_MATTER:
					  text.setColor(COLOR_BLOCKED);
					  oss += " (no matter)";
				case INVALID:
					  oss += " (invalide)";
					break;
				case NO_LEFT_CARRY:
					  oss += " (no left carry)";
					break;
				default:
					break;
				  }
			  } else {
				  text.setColor(COLOR_QUEUE);
				  oss += " (on queue)";
			  }
			  text.setString(oss);
			  text.setPosition(posX + Constant.UI_PADDING, posY + 52 + Constant.UI_PADDING + (14 * i++));
			  app.draw(text, _render);
			}
		  }

	}
}
