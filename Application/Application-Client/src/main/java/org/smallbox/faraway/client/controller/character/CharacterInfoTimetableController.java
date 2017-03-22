package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.CharacterTimetableExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterInfoTimetableController extends LuaController {

    @BindComponent
    private Game game;

    @BindLua
    private UIList listTimetable;

    private CharacterModel _selected;

    @Override
    public void onControllerUpdate() {
        if (isVisible() && _selected != null) {
            selectCharacter(_selected);
        }
    }

    public void selectCharacter(CharacterModel character) {
        _selected = character;
        displayTimetable(character);
    }

    private void displayTimetable(CharacterModel character) {
        CharacterTimetableExtra timetable = character.getExtra(CharacterTimetableExtra.class);

        game.getPlanet().getDayTimes().forEach(dayTime -> {
            View view = new UIFrame(null)
                    .setSize(32, 16);

            view.addView(new UIFrame(null)
                    .setSize(4, 16)
                    .setMargin(2, 0)
                    .setBackgroundColor(dayTime.color));

            view.addView(new UIFrame(null)
                    .setSize(32, 14)
                    .setMargin(2, 0)
                    .setPosition(8, 1)
                    .setBorderColor(0x88aadd)
                    .setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)))
                    .setOnClickListener(event -> {
                        timetable.nextState(dayTime.hour);
                        event.view.setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)));
                    }));

            listTimetable.addNextView(view);
        });
        listTimetable.switchViews();
    }

    private long getStateColor(CharacterTimetableExtra.State state) {
        switch (state) {
            case FREE: return 0x00ff00;
            case WORK: return 0xff0000;
            case SLEEP: return 0x0000ff;
        }
        return 0;
    }

}
