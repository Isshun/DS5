package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
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
	private static final int 	RESIZE_RUNNING_JOB_OCCURENCE = 20;
	
	private TextView 			_lbTitle;
	private TextView 			_lbTitle2;
	private UserInterface 		_ui;
	private TextView[] 			_entries;
	private int 				_nbRunningJob;
	private int 				_nbRunningJobCandidat;

	public PanelJobs(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT), null);

		_lbTitle = new TextView();
		_lbTitle.setCharacterSize(FONT_SIZE_TITLE);
		_lbTitle.setColor(COLOR_LABEL);
		_lbTitle.setPosition(20, 18);
		addView(_lbTitle);

		_lbTitle2 = new TextView();
		_lbTitle2.setCharacterSize(FONT_SIZE_TITLE);
		_lbTitle2.setColor(COLOR_LABEL);
		_lbTitle2.setPosition(20, 18);
		addView(_lbTitle2);

		_entries = new TextView[75];
		for (int i = 0; i < 75; i++) {
			_entries[i] = new TextView(new Vector2f(FRAME_WIDTH - 42, 16));
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
		List<Job> jobs = JobManager.getInstance().getJobs();
		int posX = 20;
		int posY = 62;
		int i = 0;

		// Display jobs
		for (Job job: jobs) {
			if (job.getCharacter() != null && Action.MOVE.equals(job.getAction()) == false) {				
				if (i < 75) {
					refreshJob(job, _entries[i++], posX, posY);
					posY += 18;
				}
			}
		}
		int nbVisibleJob = i;
		_lbTitle.setString(StringUtils.getDashedString("OCCUPATIONS", String.valueOf(nbVisibleJob), 29));
		if (update % RESIZE_RUNNING_JOB_OCCURENCE == 0) {
			_nbRunningJob = _nbRunningJobCandidat;
			_nbRunningJobCandidat = i;
		}
		if (i > _nbRunningJobCandidat) {
			_nbRunningJobCandidat = i;
		}
		if (i > _nbRunningJob) {
			_nbRunningJob = i;
		}
		
		posY = 82 + (18 * _nbRunningJob);
		_lbTitle2.setPosition(posX, posY);

		posY += 42;
		for (Job job: jobs) {
			if (job.getCharacter() == null) {				
				if (i < 75) {
					refreshJob(job, _entries[i++], posX, posY);
					posY += 18;
				}
			}
		}
		_lbTitle2.setString(StringUtils.getDashedString("IN QUEUE", String.valueOf(i - nbVisibleJob), 29));

		for (; i < 75; i++) {
			_entries[i].setVisible(false);
		}
	}

	private void refreshJob(final Job job, TextView text, int x, int y) {
		text.setVisible(true);
		text.setPosition(x, y);
		text.resetPos();
		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (job.getCharacter() != null) {
					close();
					_ui.setCharacter(job.getCharacter());
				}
			}
		});
		
		String right = JobManager.getActionName(job.getAction());
		if (job.getItem() != null) {
			right += " " + job.getItem().getLabel();
		}
		
		String left = "(on queue)";
		if (job.getCharacter() != null) {
			left = job.getCharacter().getName();
		} else if (job.getFail() > 0) {
			switch (job.getReason()) {
			case BLOCKED: left = "(blocked: #" + job.getNbBlocked() + ")"; break;
			case INTERRUPTE: left = "(interrupte)"; break;
			case NO_COMPONENTS: left = "(no matter)"; break;
			case INVALID: left = "(invalide)"; break;
			case NO_LEFT_CARRY: left = "(no left carry)"; break;
			default: break;
			}
		}
		text.setString(StringUtils.getDashedString(left, right, NB_COLUMNS));
	}
}
