package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.JobManager.Action;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.List;

public class PanelJobs extends BaseRightPanel {
	private static final int 	NB_COLUMNS = 47;
	private static final int 	RESIZE_RUNNING_JOB_OCCURENCE = 20;
	
	private TextView 			_lbTitle;
	private TextView 			_lbTitle2;
	private TextView[] 			_entries;
	private int 				_nbRunningJob;
	private int 				_nbRunningJobCandidat;

	public PanelJobs(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate(ViewFactory viewFactory) {

		_lbTitle = viewFactory.createTextView();
		_lbTitle.setCharacterSize(FONT_SIZE_TITLE);
		_lbTitle.setPosition(20, 18);
		addView(_lbTitle);

		_lbTitle2 = viewFactory.createTextView();
		_lbTitle2.setCharacterSize(FONT_SIZE_TITLE);
		_lbTitle2.setPosition(20, 18);
		addView(_lbTitle2);

		_entries = new TextView[75];
		for (int i = 0; i < 75; i++) {
			_entries[i] = viewFactory.createTextView(FRAME_WIDTH - 42, 16);
			_entries[i].setCharacterSize(14);
			addView(_entries[i]);
		}
	}

	@Override
	public void onRefresh(int update) {
		List<Job> jobs = JobManager.getInstance().getJobs();
		int posX = 20;
		int posY = 92;
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
		
		posY = 102 + (18 * _nbRunningJob);
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
					_ui.select(job.getCharacter());
				}
			}
		});
		
		String right = JobManager.getActionName(job.getAction());
		if (job.getItem() != null) {
			right += " " + job.getItem().getLabel();
		}
		
		String left = "";
		if (job.getCharacter() != null) {
			left = job.getCharacter().getName();
		} else if (job.getFail() > 0) {
			switch (job.getReason()) {
			case BLOCKED: left = "(blocked: #" + job.getNbBlocked() + ")"; break;
			case INTERRUPTE: left = "(interrupte)"; break;
			case NO_BUILD_RESOURCES:
			case NO_COMPONENTS: left = "(no matter)"; break;
			case INVALID: left = "(invalide)"; break;
			case NO_LEFT_CARRY: left = "(no left carry)"; break;
			default: break;
			}
		} else {
			switch (job.getStatus()) {
			case ABORTED: left = "(aborted)"; break;
			case COMPLETE: left = "(complete)"; break;
			case RUNNING: left = "(running)"; break;
			case WAITING: left = "(on queue)"; break;
			default: left = "(unknow)"; break;
			}
		}
		text.setString(StringUtils.getDashedString(left, right, NB_COLUMNS));
	}
}
