import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;

/**
 * Created by Alex on 22/11/2015.
 */
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
