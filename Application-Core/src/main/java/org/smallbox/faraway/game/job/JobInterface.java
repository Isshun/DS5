package org.smallbox.faraway.game.job;

import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.time.LocalDateTime;

public interface JobInterface {
    default void onInit(LocalDateTime localDateTime) {}
    default void onAction(CharacterModel character, double hourInterval, LocalDateTime localDateTime) {}
    default JobTaskReturn onGetStatus(LocalDateTime localDateTime) { return JobTaskReturn.TASK_COMPLETED; }
}
