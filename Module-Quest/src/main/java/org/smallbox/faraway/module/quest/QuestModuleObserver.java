package org.smallbox.faraway.module.quest;

import org.smallbox.faraway.core.engine.module.ModuleObserver;

/**
 * Created by Alex on 19/07/2016.
 */
public interface QuestModuleObserver extends ModuleObserver {
    void onOpenQuest(QuestModel quest);
    void onCloseQuest(QuestModel quest);
}
