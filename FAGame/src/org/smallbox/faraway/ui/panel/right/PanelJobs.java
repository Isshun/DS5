package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.LinkFocusListener;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.*;
import org.smallbox.faraway.ui.panel.BaseRightPanel;

public class PanelJobs extends BaseRightPanel {
	private static class JobEntry {
		public View 			frameJob;
		public UILabel			lbJob;
        public UILabel          lbStatus;
		public UILabel          lbMessage;
    }

	private JobEntry[] 			_entries;

	public PanelJobs(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, "data/ui/panels/jobs.yml");
	}

	@Override
	public void onLayoutLoaded(LayoutModel layout) {
		FrameLayout frameEntries = (FrameLayout)findById("frame_jobs");
		_entries = new JobEntry[75];
		for (int i = 0; i < _entries.length; i++) {
			final int index = i;
			ViewFactory.getInstance().load("data/ui/panels/view_job_info.yml", view -> {
                _entries[index] = new JobEntry();
                _entries[index].frameJob = view;
                _entries[index].lbJob = (UILabel) view.findById("lb_job");
                _entries[index].lbStatus = (UILabel) view.findById("lb_job_status");
                _entries[index].lbMessage = (UILabel) view.findById("lb_job_message");
                view.setPosition(0, index * 24);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                view.setOnFocusListener(new LinkFocusListener());
                frameEntries.addView(view);
            });
		}
	}

	@Override
	public void onRefresh(int update) {
		// Display jobs
		int index = 0;
		for (BaseJobModel job: JobManager.getInstance().getJobs()) {
            if (index < _entries.length) {
                JobEntry entry = _entries[index];
                entry.frameJob.setVisible(true);
                entry.lbJob.setString(job.getLabel());

//                if (job.getMessage() != null) {
//                    entry.lbMessage.setVisible(true);
//                    entry.lbMessage.setString(job.getMessage());
//                } else {
//                    entry.lbMessage.setVisible(false);
//                }

                if (job.getCharacter() != null) {
                    entry.lbStatus.setString(job.getCharacter().getName());
                } else {
                    switch (job.getStatus()) {
                        case ABORTED: entry.lbStatus.setString("(aborted)"); break;
                        case COMPLETE: entry.lbStatus.setString("(close)"); break;
                        case RUNNING: entry.lbStatus.setString("(running)"); break;
                        case WAITING: entry.lbStatus.setString("(on queue)"); break;
                        default: entry.lbStatus.setString("(unknow)"); break;
                    }
                }

                index++;
            }
		}

        for (; index < _entries.length; index++) {
            _entries[index].frameJob.setVisible(false);
        }
	}
}
