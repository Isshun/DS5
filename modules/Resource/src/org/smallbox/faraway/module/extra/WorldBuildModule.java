//package org.smallbox.faraway.module.extra;
//
//import org.json.JSONObject;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.game.GameInfo;
//import org.smallbox.faraway.core.game.GameManager;
//import org.smallbox.faraway.core.engine.module.ModuleBase;
//import org.smallbox.faraway.core.util.FileUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Alex on 05/07/2015.
// */
//public class WorldBuildModule extends ModuleBase {
//    @Override
//    protected boolean loadOnStart() {
//        return true;
//    }
//
//    @Override
//    protected void onLoaded() {
//    }
//
//    @Override
//    protected void onUpdate(int tick) {
//    }
//
//    @Override
//    public void onReloadUI() {
//    }
//
//    @Override
//    public void onCustomEvent(String tag, Object object) {
//        if ("on_load_menu_create".equals(tag)) {
//            load();
//        }
//        if ("load_game.game".equals(tag) && object instanceof GameInfo) {
//            _game = (GameInfo) object;
//        }
//        if ("load_game.save".equals(tag) && object instanceof GameInfo.GameSaveInfo) {
//            _save = (GameInfo.GameSaveInfo) object;
//        }
//        if ("load_game.load".equals(tag) && _game != null && _save != null) {
//            GameManager.getInstance().loadGame(_game, _save);
//        }
//        if ("load_game.last_game".equals(tag) && !_games.isEmpty() && !_games.get(0).saveFiles.isEmpty()) {
//            GameManager.getInstance().loadGame(_games.get(0), _games.get(0).saveFiles.get(0));
//        }
//    }
//}