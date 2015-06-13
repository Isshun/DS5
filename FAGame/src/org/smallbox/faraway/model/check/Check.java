package org.smallbox.faraway.model.check;

import org.smallbox.faraway.manager.JobManager;

public interface Check {
	boolean create(JobManager jobManager);
}
