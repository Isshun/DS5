package org.smallbox.faraway.client.controller.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.widgets.*;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerComplete;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.save.GameFileManager;
import org.smallbox.faraway.core.save.GameInfo;
import org.smallbox.faraway.core.save.GameLoadManager;
import org.smallbox.faraway.core.save.GameSaveInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ApplicationObject
public class MenuLoadController extends LuaController {
    @Inject private GameManager gameManager;
    @Inject private GameLoadManager gameLoadManager;
    @Inject private GameFactory gameFactory;
    @Inject private GameFileManager gameFileManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private MenuCrewController menuCrewController;
    @Inject private MenuMainController menuMainController;
    @Inject private DataManager dataManager;

    @BindLua private UIList listGames;
    @BindLua private UIList listSaves;
    @BindLua private UIImage imgPlanet;
    @BindLua private View infoPlanet;
    @BindLua private UILabel lbInfoName;
    @BindLua private UIList listInfoRegions;
    @BindLua private View loadDetail;
    @BindLua private View gameDetail;
    @BindLua private UILabel lbGamePlanet;
    @BindLua private UILabel lbGameRegion;
    @BindLua private UILabel lbGameSize;

    @BindLua private UIImage imgDetail;
    @BindLua private UILabel lbDetailName;
    @BindLua private UILabel lbDetailDuration;
    @BindLua private UILabel lbDetailRealDuration;
    @BindLua private UILabel lbDetailCrew;

    private GameInfo gameInfo;
    private GameSaveInfo saveInfo;

    @OnApplicationLayerComplete
    private void afterApplicationLayerInit() {
        gameFileManager.buildGameList().stream().sorted((o1, o2) -> o2.date.compareTo(o1.date)).forEach(gameInfo -> {
            CompositeView viewPlanet = listGames.createFromTemplate(CompositeView.class);
            viewPlanet.setId(gameInfo.name);

            UILabel lbPlanet = viewPlanet.findLabel("lb_game");
            lbPlanet.setText(gameInfo.label);
            lbPlanet.getEvents().setOnClickListener(() -> selectGame(gameInfo));

            listGames.addNextView(viewPlanet);
        });
        listGames.switchViews();
    }

    private void selectGame(GameInfo gameInfo) {
        this.gameInfo = gameInfo;

        gameDetail.setVisible(true);

        lbGamePlanet.setText(gameInfo.planet.label);
        lbGameRegion.setText(gameInfo.region.label);
        lbGameSize.setText(buildWorldSizeLabel(gameInfo));

        gameInfo.saveFiles.stream().sorted((o1, o2) -> o2.date.compareTo(o1.date)).forEach(saveInfo -> {
            CompositeView viewSave = listSaves.createFromTemplate(CompositeView.class);
            viewSave.setId(saveInfo.filename);

            UILabel lbPlanet = viewSave.findLabel("lb_save");
            lbPlanet.setText(saveInfo.label);
            lbPlanet.getEvents().setOnClickListener(() -> selectSave(saveInfo));

            listSaves.addNextView(viewSave);
        });
        listSaves.switchViews();
    }

    private String buildWorldSizeLabel(GameInfo gameInfo) {
        if (gameInfo.worldWidth * gameInfo.worldHeight <= 2500) {
            return "Small";
        }
        else if (gameInfo.worldWidth * gameInfo.worldHeight >= 2500) {
            return "Big";
        }
        return "Medium";
    }

    private void selectSave(GameSaveInfo saveInfo) {
        this.saveInfo = saveInfo;

        loadDetail.setVisible(true);
        lbDetailName.setText(saveInfo.filename);
        lbDetailDuration.setText(LocalTime.ofSecondOfDay(saveInfo.duration).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lbDetailRealDuration.setText(LocalTime.ofSecondOfDay(saveInfo.duration).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lbDetailCrew.setText(String.valueOf(saveInfo.crew));

        // TODO: dispose
        Texture texture = gameFileManager.getScreenshot(saveInfo);
        if (texture != null) {
            Sprite sprite = new Sprite(texture);
            sprite.setSize(imgDetail.getWidth(), imgDetail.getHeight());
            imgDetail.setImage(sprite);
        }
    }

    @BindLuaAction
    private void onActionBack(View view) {
        setVisible(false);
        menuMainController.setVisible(true);
    }

    @BindLuaAction
    private void onActionLoad(View view) {
        if (gameInfo != null && saveInfo != null) {
            setVisible(false);
            gameManager.loadGame(gameInfo, saveInfo);
        }
    }

//    @GameShortcut("ui/up")
//    public void onPressUp() {
//        selectGame(safePlanet(dataManager.planets.indexOf(planet) - 1));
//    }
//
//    @GameShortcut("ui/down")
//    public void onPressDown() {
//        selectGame(safePlanet(dataManager.planets.indexOf(planet) + 1));
//    }

}
