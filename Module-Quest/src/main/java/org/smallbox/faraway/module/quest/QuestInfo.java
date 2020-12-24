package org.smallbox.faraway.module.quest;

import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;

public class QuestInfo extends ObjectInfo {
    public String   label;
    public String   openMessage;
    public String[] openOptions;

    public interface OnQuestCheckListener {
        boolean onQuestCheck(QuestModel quest);
    }

    public interface OnQuestStartListener {
        void onQuestStart(QuestModel quest, int optionIndex);
    }

    public interface OnQuestCloseListener {
        boolean onQuestClose(QuestModel quest);
    }

    public OnQuestCheckListener onQuestCheckListener;
    public OnQuestStartListener onQuestStartListener;
    public OnQuestCloseListener onQuestCloseListener;
}
