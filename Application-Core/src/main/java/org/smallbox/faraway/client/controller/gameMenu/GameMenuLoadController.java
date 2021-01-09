package org.smallbox.faraway.client.controller.gameMenu;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.save.GameFileManager;
import org.smallbox.faraway.core.game.save.GameSaveInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@GameObject
public class GameMenuLoadController extends LuaController {

    @Inject
    private Game game;

    @Inject
    private GameManager gameManager;

    @Inject
    private GameFileManager gameFileManager;

    @Inject
    private GameMenuPauseController gameMenuPauseController;

    @BindLua private UIList loadEntries;
    @BindLua private View loadDetail;
    @BindLua private UILabel lbDetailName;
    @BindLua private UILabel lbDetailDuration;
    @BindLua private UILabel lbDetailRealDuration;
    @BindLua private UILabel lbDetailCrew;

    private GameSaveInfo gameSaveInfo;

    protected void onControllerUpdate() {
        loadEntries.getViews().clear();
        gameFileManager.getSaves().forEach(gameSaveInfo -> {
            UILabel uiLabel = UILabel.createFast(gameSaveInfo.label, Colors.BLUE_LIGHT_3);
            uiLabel.setTextSize(20);
            uiLabel.setSize(400, 40);
            uiLabel.setFocusBackgroundColor(0x225588ff);
            uiLabel.getEvents().setOnClickListener((x, y) -> displaySave(gameSaveInfo));
            uiLabel.getStyle().setBackgroundColor(Colors.BLUE_DARK_1);
            loadEntries.getViews().add(uiLabel);

            View view = new UIFrame(null);
            view.setSize(400, 5);
            loadEntries.getViews().add(view);
        });

    }

    private void displaySave(GameSaveInfo gameSaveInfo) {
        this.gameSaveInfo = gameSaveInfo;
        loadDetail.setVisible(true);
        lbDetailName.setText(gameSaveInfo.filename);
        lbDetailDuration.setText(LocalTime.ofSecondOfDay(gameSaveInfo.duration).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lbDetailRealDuration.setText(LocalTime.ofSecondOfDay(gameSaveInfo.duration).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lbDetailCrew.setText(String.valueOf(gameSaveInfo.crew));
    }

    @GameShortcut(key = Input.Keys.ESCAPE)
    public void onEscape() {
        if (isVisible()) {
            setVisible(false);
            gameMenuPauseController.setVisible(true);
        }
    }

    @BindLuaAction
    public void onActionSave(View view) {
        if (gameSaveInfo != null) {
            setVisible(false);
            gameManager.loadGame(game.getInfo(), gameSaveInfo, null);
        }
    }

}
