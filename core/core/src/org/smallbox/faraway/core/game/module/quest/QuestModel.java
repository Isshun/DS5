package org.smallbox.faraway.core.game.module.quest;

import org.smallbox.faraway.core.game.modelInfo.QuestInfo;

/**
 * Created by Alex on 31/08/2015.
 */
public class QuestModel {
    public final QuestInfo  info;
    public boolean          isOpen;
    public int              optionIndex;

    public QuestModel(QuestInfo questInfo) {
        this.info = questInfo;
        this.isOpen = true;
    }
}
