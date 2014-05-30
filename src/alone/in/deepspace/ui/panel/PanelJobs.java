package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.StringUtils;

public class PanelJobs extends UserSubInterface {
	private static final Color 	COLOR_BUILD = new Color(170, 128, 64);
	private static final Color 	COLOR_BLOCKED = new Color(255, 20, 20);
	private static final Color 	COLOR_DESTROY = new Color(200, 20, 20);
	private static final Color 	COLOR_STORE = new Color(180, 100, 255);
	private static final Color 	COLOR_QUEUE = new Color(255, 255, 20);
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int 	NB_COLUMNS = 47;
	private TextView _lbTitle;
	private UserInterface _ui;
	private TextView[] _entries;

	public PanelJobs(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));

		_lbTitle = new TextView();
		_lbTitle.setCharacterSize(FONT_SIZE);
		_lbTitle.setColor(COLOR_LABEL);
		_lbTitle.setPosition(20, 18);
		addView(_lbTitle);

		_entries = new TextView[75];
		for (int i = 0; i < 75; i++) {
			_entries[i] = new TextView(new Vector2f(FRAME_WIDTH - 42, 16));
			_entries[i].setPosition(20, 52 + Constant.UI_PADDING + (18 * i));
			_entries[i].setCharacterSize(14);
			_entries[i].setColor(COLOR_TEXT);
			_entries[i].setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					((TextView)view).setStyle(TextView.REGULAR);
					((TextView)view).setColor(COLOR_TEXT);
					//view.setBackgroundColor(null);
				}

				@Override
				public void onEnter(View view) {
					((TextView)view).setStyle(TextView.UNDERLINED);
					((TextView)view).setColor(COLOR_ACTIVE);
					//view.setBackgroundColor(new Color(40, 40, 80));
				}
			});
			addView(_entries[i]);
		}
	}

	public void setUI(UserInterface userInterface) {
		_ui = userInterface;
	}

	@Override
	public void onRefresh(int update) {
		int posX = 20;
		int posY = 0;

		// Display jobs
		List<Job> jobs = JobManager.getInstance().getJobs();
		int nbVisibleJob = JobManager.getInstance().getNbVisibleJob();

		_lbTitle.setString(StringUtils.getDashedString("OCCUPATIONS", String.valueOf(nbVisibleJob), 29));
		
		int i = 0;
		for (Job job: jobs) {
			if (Action.MOVE.equals(job.getAction()) == false) {				
				if (i < 75) {
					final Job j = job;
					_entries[i].setVisible(true);
					_entries[i].setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							if (j.getCharacter() != null) {
								close();
								_ui.setCharacter(j.getCharacter());
							}
						}
					});
					
//					String oss = (job.getId()  < 10 ? "#0" : "#") + job.getId()
//							+ " - " + JobManager.getActionName(job.getAction());
//					if (job.getItem() != null) {
//						oss += " " + job.getItem().getLabel();
//					}
//					if (job.getCharacter() != null) {
//						text.setColor(job.getColor());
//						oss += " (" + job.getCharacter().getName() + ")";
//					} else if (job.getFail() > 0) {
//						switch (job.getReason()) {
//						case BLOCKED:
//							text.setColor(COLOR_BLOCKED);
//							oss += " (blocked: #" + job.getBlocked() + ")";
//							break;
//						case INTERRUPTE:
//							oss += " (interrupte)";
//							break;
//						case NO_MATTER:
//							text.setColor(COLOR_BLOCKED);
//							oss += " (no matter)";
//							break;
//						case INVALID:
//							oss += " (invalide)";
//							break;
//						case NO_LEFT_CARRY:
//							oss += " (no left carry)";
//							break;
//						default:
//							break;
//						}
//					} else {
//						text.setColor(COLOR_QUEUE);
//						oss += " (on queue)";
//					}
					
					String right = JobManager.getActionName(job.getAction());
					if (job.getItem() != null) {
						right += " " + job.getItem().getLabel();
					}
					
					String left = "(on queue)";
					if (job.getCharacter() != null) {
//						_entries[i].setColor(job.getColor());
						left = job.getCharacter().getName();
					} else if (job.getFail() > 0) {
						switch (job.getReason()) {
						case BLOCKED:
//							_entries[i].setColor(COLOR_BLOCKED);
							left = " (blocked: #" + job.getBlocked() + ")";
							break;
						case INTERRUPTE:
							left = " (interrupte)";
							break;
						case NO_COMPONENTS:
//							_entries[i].setColor(COLOR_BLOCKED);
							left = " (no matter)";
							break;
						case INVALID:
							left = " (invalide)";
							break;
						case NO_LEFT_CARRY:
							left = " (no left carry)";
							break;
						default:
							break;
						}
					}
//					_entries[i].setColor(COLOR_TEXT);
					_entries[i].setString(StringUtils.getDashedString(left, right, NB_COLUMNS));
					
					i++;
				}
			}
		}
		
		for (; i < 75; i++) {
			_entries[i].setVisible(false);
		}
	}
}
