package org.smallbox.faraway.module.quest;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UILabel;

/**
 * Created by Alex on 30/08/2015.
 */
public class QuestModuleUI {
    private final UIFrame _panel;
    private int                 _nbOptions;
    private QuestModel          _quest;

    public QuestModuleUI(UIFrame panel) {
        _panel = panel;

        _panel.findById("frame_message").setVisible(false);

        UILabel lbQuest = ViewFactory.getInstance().createTextView();
        lbQuest.setText("quest");
        lbQuest.setTextSize(14);
        ((UIFrame)_panel.findById("frame_list")).addView(lbQuest);
    }

    public void onOpenQuest(QuestModel quest) {
        _quest = quest;

        if (quest != null && quest.message != null) {
            displayMessage(quest);

            // Display options
            String[] options = quest.options;
            for (int i = 0; i < options.length; i++) {
                final int optionIndex = i + 1;
                final UILabel lbOption = (UILabel) _panel.findById("lb_opt_" + (i + 1));
                lbOption.setText(" [" + (i + 1) + "] " + options[i]);
                lbOption.setVisible(true);
                lbOption.setOnClickListener(view -> selectOption(quest, optionIndex));
            }

            _nbOptions = options.length;
        }
    }

    public void onCloseQuest(QuestModel quest) {
        _quest = quest;

        if (quest != null && quest.message != null) {
            displayMessage(quest);

            // Display 'OK' option
            final UILabel lbOption = (UILabel) _panel.findById("lb_opt_1");
            lbOption.setText(" [1] OK");
            lbOption.setVisible(true);
            lbOption.setOnClickListener(view -> _panel.findById("frame_message").setVisible(false));

            _nbOptions = 1;
        }
    }

    private void displayMessage(QuestModel quest) {
        // Open frame
        _panel.findById("frame_message").setVisible(true);

        // Display message
        ((UILabel)_panel.findById("lb_message")).setText(quest.message);

        // Clear options
        for (int i = 0; i < 5; i++) {
            _panel.findById("lb_opt_" + (i + 1)).setVisible(false);
        }
    }

    private void selectOption(QuestModel quest, int optionIndex) {
        _panel.findById("frame_message").setVisible(false);

        ((QuestModule) ModuleManager.getInstance().getModule(QuestModule.class)).selectQuestionOption(quest, optionIndex);
    }

    public boolean	onKey(GameEventListener.Key key) {
        if (_panel.findById("frame_message").isVisible()) {
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
