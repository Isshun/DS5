package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.CharacterTimetableExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

@GameObject
public class CharacterInfoTimetableController extends LuaController {
    @Inject private Game game;
    @Inject private GameTime gameTime;

    @BindLua
    private UIList listTimetable;

    @BindLua
    private View marker;

    private CharacterModel _selected;

    @OnGameUpdate(runOnMainThread = true)
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
        if (character.hasExtra(CharacterTimetableExtra.class)) {
            CharacterTimetableExtra timetable = character.getExtra(CharacterTimetableExtra.class);

            if (listTimetable.getViews().isEmpty()) {
                game.getPlanet().getDayTimes().forEach(dayTime -> {

                    UIFrame view = listTimetable.createFromTemplate(UIFrame.class);

                    View subView = view.find("view_timetable");
                    subView.getStyle().setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)));
                    subView.getEvents().setOnClickListener(() -> {
                        timetable.nextState(dayTime.hour);
                        subView.getStyle().setBackgroundColor(getStateColor(timetable.getState(dayTime.hour)));
                    });

                    UILabel lbTimetable = view.findLabel("lb_timetable");
                    lbTimetable.setText(dayTime.hour + "h");

                    listTimetable.addView(view);
                });
            }

            marker.getGeometry().setPositionY((22 * 24) * (gameTime.getHour() * 60 + gameTime.getMinute()) / (24 * 60));
        }
    }

    private int getStateColor(CharacterTimetableExtra.State state) {
        switch (state) {
            case FREE:
                return 0x225aff5a;
            case WORK:
                return 0x22ff7a7a;
            case SLEEP:
                return 0x229abbff;
        }
        return 0;
    }

}
