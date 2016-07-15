package org.smallbox.faraway.module.quest;

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
