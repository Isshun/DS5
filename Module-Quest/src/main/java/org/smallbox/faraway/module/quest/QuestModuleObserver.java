package org.smallbox.faraway.module.quest;

import org.smallbox.faraway.core.engine.module.ModuleObserver;

public interface QuestModuleObserver extends ModuleObserver {
    void onOpenQuest(QuestModel quest);
    void onCloseQuest(QuestModel quest);
}
