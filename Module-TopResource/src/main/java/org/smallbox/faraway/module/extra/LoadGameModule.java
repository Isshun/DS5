package org.smallbox.faraway.module.extra;

import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.ApplicationModule;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LoadGameModule extends ApplicationModule {
    private List<GameInfo>          _games = new ArrayList<>();

    private GameInfo                _currentGame;
    private GameInfo.GameSaveInfo   _currentSave;

    public List<GameInfo> getGames() {
        return _games;
    }

    private void loadAction() {
        _games.clear();
        FileUtils.list(FileUtils.getSaveDirectory()).stream().filter(File::isDirectory).forEach(gameDirectory -> {
            File file = new File(gameDirectory, "game.json");
            if (file.exists()) {
                try {
                    Log.debug("Load game directory: " + gameDirectory.getName());
                    GameInfo info = GameInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8)));
                    if (info != null) {
                        _games.add(info);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Application.notify(observer -> observer.onCustomEvent("on_refresh_save_directory", null));
    }

    @Override
    public void onCustomEvent(String tag, Object object) {
        if ("on_load_menu_create".equals(tag)) {
            loadAction();
        }
        if ("load_game.game".equals(tag) && object instanceof GameInfo) {
            _currentGame = (GameInfo) object;
        }
        if ("load_game.onSave".equals(tag) && object instanceof GameInfo.GameSaveInfo) {
            _currentSave = (GameInfo.GameSaveInfo) object;
        }
        if ("load_game.onLoadModule".equals(tag) && _currentGame != null && _currentSave != null) {
            Application.notify(observer -> observer.onGameLoad(_currentGame, _currentSave));
        }
        if ("load_game.last_game".equals(tag)) {
            loadAction();
            _games.stream()
                    .flatMap(gameInfo -> gameInfo.saveFiles.stream())
                    .sorted((o1, o2) -> o2.date.compareTo(o1.date))
                    .findFirst()
                    .ifPresent(saveInfo -> Application.notify(observer -> observer.onGameLoad(saveInfo.game, saveInfo)));
        }
    }
}