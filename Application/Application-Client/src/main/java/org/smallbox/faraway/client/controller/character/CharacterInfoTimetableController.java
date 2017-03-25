package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.engine.ColorUtils;
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

    @BindLua
    private View marker;

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

        if (listTimetable.getViews().isEmpty()) {
            game.getPlanet().getDayTimes().forEach(dayTime -> {
                View view = new UIFrame(null)
                        .setSize(32, 22);

                view.addView(new UIFrame(null)
                        .setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)))
                        .setSize(300, 21)
                        .setOnClickListener(event -> {
                            timetable.nextState(dayTime.hour);
                            event.view.setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)));
                        }));

//                view.addView(new UIFrame(null)
//                        .setSize(4, 16)
//                        .setPosition(38, 1)
//                        .setMargin(2, 0)
//                        .setBackgroundColor(dayTime.color));
//
                view.addView(new UILabel(null)
                        .setText(dayTime.hour + "h")
                        .setTextColor(ColorUtils.COLOR2)
                        .setTextSize(14)
                        .setPadding(6));
//
//                view.addView(new UIFrame(null)
//                        .setSize(32, 14)
//                        .setMargin(2, 0)
//                        .setPosition(38, 1)
//                        .setBorderColor(dayTime.hour == game.getTime().getHour() ? 0xff0000 : 0x88aadd)
//                        .setBackgroundColor()
//                        .setOnClickListener(event -> {
//                            timetable.nextState(dayTime.hour);
//                            event.view.setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)));
//                        }));

                listTimetable.addView(view);
            });
        }

        marker.setPositionY((22 * 24) * (game.getTime().getHour() * 60 + game.getTime().getMinute()) / (24 * 60));
    }

    private long getStateColor(CharacterTimetableExtra.State state) {
        switch (state) {
            case FREE: return 0x225aff5a;
            case WORK: return 0x22ff7a7a;
            case SLEEP: return 0x229abbff;
        }
        return 0;
    }

}
