package org.smallbox.faraway.client.controller.gameMenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.save.GameFileManager;
import org.smallbox.faraway.core.save.GameSaveInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@GameObject
public class GameMenuLoadController extends LuaController {
    @Inject private Game game;
    @Inject private GameManager gameManager;
    @Inject private GameFileManager gameFileManager;
    @Inject private GameMenuPauseController gameMenuPauseController;

    @BindLua private UIList loadEntries;
    @BindLua private View loadDetail;
    @BindLua private UIImage imgDetail;
    @BindLua private UILabel lbDetailName;
    @BindLua private UILabel lbDetailDuration;
    @BindLua private UILabel lbDetailRealDuration;
    @BindLua private UILabel lbDetailCrew;

    private GameSaveInfo gameSaveInfo;

    protected void onControllerUpdate() {
        gameFileManager.getSaves().stream().sorted((o1, o2) -> o2.date.compareTo(o1.date)).forEach(gameSaveInfo -> {
            CompositeView viewEntry = loadEntries.createFromTemplate(CompositeView.class);
            viewEntry.findLabel("lb_entry").setText(gameSaveInfo.label);
            viewEntry.findLabel("lb_entry").getEvents().setOnClickListener(() -> displaySave(gameSaveInfo));
            loadEntries.addNextView(viewEntry);
        });
        loadEntries.switchViews();
    }

    private void displaySave(GameSaveInfo gameSaveInfo) {
        this.gameSaveInfo = gameSaveInfo;
        loadDetail.setVisible(true);
        lbDetailName.setText(gameSaveInfo.filename);
        lbDetailDuration.setText(LocalTime.ofSecondOfDay(gameSaveInfo.duration).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lbDetailRealDuration.setText(LocalTime.ofSecondOfDay(gameSaveInfo.duration).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lbDetailCrew.setText(String.valueOf(gameSaveInfo.crew));

        // TODO: dispose
        Sprite sprite = new Sprite(gameFileManager.getScreenshot(gameSaveInfo));
        sprite.setSize(imgDetail.getWidth(), imgDetail.getHeight());
        imgDetail.setImage(sprite);
    }

    @GameShortcut("escape")
    public void onEscape() {
        if (isVisible()) {
            setVisible(false);
            gameMenuPauseController.setVisible(true);
        }
    }

    @BindLuaAction
    public void onActionLoad(View view) {
        if (gameSaveInfo != null) {
            setVisible(false);
            gameManager.loadGame(game.getInfo(), gameSaveInfo, null);
        }
    }

}
