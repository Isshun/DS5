package org.smallbox.faraway.module.job;

import org.smallbox.faraway.core.module.character.model.base.CharacterModel;

/**
 * Created by Alex on 11/12/2016.
 */
public interface JobTask {
    boolean onExecuteTask(CharacterModel character);
}
