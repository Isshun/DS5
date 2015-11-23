package org.smallbox.faraway.module.extra;

import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 05/07/2015.
 */
public class LoadGameModule extends GameModule {
    private List<GameInfo>          _games = new ArrayList<>();

    private GameInfo                _currentGame;
    private GameInfo.GameSaveInfo   _currentSave;

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onLoaded(Game game) {
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onReloadUI() {
    }

    public List<GameInfo> getGames() {
        return _games;
    }

    private void load() {
        _games.clear();
        FileUtils.list(new File("saves/")).stream().filter(File::isDirectory).forEach(gameDirectory -> {
            File file = new File(gameDirectory, "game.json");
            if (file.exists()) {
                try {
                    System.out.println("Load game directory: " + gameDirectory.getName());
                    GameInfo info = GameInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8)));
                    if (info != null) {
                        _games.add(info);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Application.getInstance().notify(observer -> observer.onCustomEvent("on_refresh_save_directory", null));
    }

    @Override
    public void onCustomEvent(String tag, Object object) {
        if ("on_load_menu_create".equals(tag)) {
            load();
        }
        if ("load_game.game".equals(tag) && object instanceof GameInfo) {
            _currentGame = (GameInfo) object;
        }
        if ("load_game.save".equals(tag) && object instanceof GameInfo.GameSaveInfo) {
            _currentSave = (GameInfo.GameSaveInfo) object;
        }
        if ("load_game.load".equals(tag) && _currentGame != null && _currentSave != null) {
            GameManager.getInstance().loadGame(_currentGame, _currentSave);
        }
        if ("load_game.last_game".equals(tag)) {
            GameInfo gameInfo = null;
            GameInfo.GameSaveInfo saveInfo = null;
            for (GameInfo game: _games) {
                for (GameInfo.GameSaveInfo save: game.saveFiles) {
                    if (saveInfo == null || save.date.after(saveInfo.date)) {
                        saveInfo = save;
                        gameInfo = game;
                    }
                }
            }
            if (saveInfo != null) {
                GameManager.getInstance().loadGame(gameInfo, saveInfo);
            }
        }
    }
}