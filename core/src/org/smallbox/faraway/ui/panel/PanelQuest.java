package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.extra.QuestManager;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 20/06/2015.
 */
public class PanelQuest extends BasePanel {
    private int _nbOptions;
    private QuestManager.QuestModel _quest;

    public PanelQuest() {
        super(null, null, 0, 0, 0, 0, "data/ui/panels/quest.yml");
        setAlwaysVisible(true);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        findById("frame_message").setVisible(false);

        UILabel lbQuest = ViewFactory.getInstance().createTextView();
        lbQuest.setString("quest");
        lbQuest.setCharacterSize(14);
        ((FrameLayout) findById("frame_list")).addView(lbQuest);
    }

    @Override
    public void onOpenQuest(QuestManager.QuestModel quest) {
        _quest = quest;

        if (quest != null && quest.message != null) {
            displayMessage(quest);

            // Display options
            String[] options = quest.options;
            for (int i = 0; i < options.length; i++) {
                final int optionIndex = i + 1;
                final UILabel lbOption = (UILabel) findById("lb_opt_" + (i + 1));
                lbOption.setString(" [" + (i + 1) + "] " + options[i]);
                lbOption.setVisible(true);
                lbOption.setOnClickListener(view -> selectOption(quest, optionIndex));
            }

            _nbOptions = options.length;
        }
    }

    @Override
    public void onCloseQuest(QuestManager.QuestModel quest) {
        _quest = quest;

        if (quest != null && quest.message != null) {
            displayMessage(quest);

            // Display 'OK' option
            final UILabel lbOption = (UILabel) findById("lb_opt_1");
            lbOption.setString(" [1] OK");
            lbOption.setVisible(true);
            lbOption.setOnClickListener(view -> findById("frame_message").setVisible(false));

            _nbOptions = 1;
        }
    }

    private void displayMessage(QuestManager.QuestModel quest) {
        // Open frame
        findById("frame_message").setVisible(true);

        // Display message
        ((UILabel) findById("lb_message")).setString(quest.message);

        // Clear options
        for (int i = 0; i < 5; i++) {
            findById("lb_opt_" + (i + 1)).setVisible(false);
        }
    }

    private void selectOption(QuestManager.QuestModel quest, int optionIndex) {
        findById("frame_message").setVisible(false);

        ((QuestManager)Game.getInstance().getManager(QuestManager.class)).selectQuestionOption(quest, optionIndex);
    }

    public boolean	checkKey(GameEventListener.Key key) {
        if (findById("frame_message").isVisible()) {
            switch (key) {
                case D_1:
                    selectOption(_quest, 1);
                    return true;
                case D_2:
                    selectOption(_quest, 2);
                    return true;
                case D_3:
                    selectOption(_quest, 3);
                    return true;
                case D_4:
                    selectOption(_quest, 4);
                    return true;
                case D_5:
                    selectOption(_quest, 5);
                    return true;
            }
        }
        return false;
    }

}
