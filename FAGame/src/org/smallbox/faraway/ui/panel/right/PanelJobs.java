package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.BaseRightPanel;
import org.smallbox.faraway.util.StringUtils;

import java.util.Collection;

public class PanelJobs extends BaseRightPanel {
	private static final int 	NB_COLUMNS = 47;
	private static final int 	RESIZE_RUNNING_JOB_OCCURENCE = 20;
	
	private UILabel _lbTitle;
	private UILabel _lbTitle2;
	private UILabel[] 			_entries;
	private int 				_nbRunningJob;
	private int 				_nbRunningJobCandidat;

	public PanelJobs(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, "data/ui/panels/jobs.yml");
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

		_entries = new UILabel[75];
		for (int i = 0; i < 75; i++) {
			_entries[i] = viewFactory.createTextView(FRAME_WIDTH - 42, 16);
			_entries[i].setCharacterSize(14);
			addView(_entries[i]);
		}
	}

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
    }

	@Override
	public void onRefresh(int update) {
        Collection<BaseJobModel> jobs = JobManager.getInstance().getJobs();

        FrameLayout frameJobs = (FrameLayout)findById("frame_jobs");
        frameJobs.removeAllViews();

		// Display jobs
		int index = 0;
		for (BaseJobModel job: jobs) {
            UILabel lbJob = ViewFactory.getInstance().createTextView(200, 30);
            lbJob.setString(job.getLabel());
            lbJob.setCharacterSize(14);
            lbJob.setPosition(0, 30 * index++);
            frameJobs.addView(lbJob);
//			if (job.getCharacter() != null && job.isVisibleInUI()) {
//				if (i < 75) {
//					refreshJob(job, _entries[i++], posX, posY);
//					posY += 18;
//				}
//			}
		}
//		int nbVisibleJob = i;
//		_lbTitle.setString(StringUtils.getDashedString("OCCUPATIONS", String.valueOf(nbVisibleJob), 29));
//		if (update % RESIZE_RUNNING_JOB_OCCURENCE == 0) {
//			_nbRunningJob = _nbRunningJobCandidat;
//			_nbRunningJobCandidat = i;
//		}
//		if (i > _nbRunningJobCandidat) {
//			_nbRunningJobCandidat = i;
//		}
//		if (i > _nbRunningJob) {
//			_nbRunningJob = i;
//		}
//
//		posY = 102 + (18 * _nbRunningJob);
//		_lbTitle2.setPosition(posX, posY);
//
//		posY += 42;
//		for (BaseJob job: jobs) {
//			if (job.getCharacter() == null) {
//				if (i < 75) {
//					refreshJob(job, _entries[i++], posX, posY);
//					posY += 18;
//				}
//			}
//		}
//		_lbTitle2.setString(StringUtils.getDashedString("IN QUEUE", String.valueOf(i - nbVisibleJob), 29));
//
//		for (; i < 75; i++) {
//			_entries[i].setString("");
//			_entries[i].setVisible(false);
//		}
	}

	private void refreshJob(final BaseJobModel job, UILabel text, int x, int y) {
		text.setVisible(true);
		text.setPosition(x, y);
		text.resetPos();
		text.setOnClickListener(view -> {
            if (job.getCharacter() != null) {
                close();
                _ui.getSelector().select(job.getCharacter());
            }
        });
		
		String right = job.getLabel();
//		String right = JobManager.getActionName(job.getAction());
//		if (job.getItem() != null) {
//			right += " " + job.getItem().getLabel();
//		}

		String left = "";
		if (job.getCharacter() != null) {
			left = job.getCharacter().getName();
		} else if (job.getFail() > 0) {
			switch (job.getReason()) {
			case BLOCKED: left = "(blocked: #" + job.getNbBlocked() + ")"; break;
			case INTERRUPT: left = "(interrupte)"; break;
			case NO_BUILD_RESOURCES:
			case NO_COMPONENTS: left = "(no progress)"; break;
			case INVALID: left = "(invalide)"; break;
			case NO_LEFT_CARRY: left = "(no left carry)"; break;
			default: break;
			}
		} else {
			switch (job.getStatus()) {
			case ABORTED: left = "(aborted)"; break;
			case COMPLETE: left = "(close)"; break;
			case RUNNING: left = "(running)"; break;
			case WAITING: left = "(on _queue)"; break;
			default: left = "(unknow)"; break;
			}
		}
		text.setString(StringUtils.getDashedString(left, right, NB_COLUMNS));
	}
}
