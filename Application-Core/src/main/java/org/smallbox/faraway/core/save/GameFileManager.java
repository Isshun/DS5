package org.smallbox.faraway.core.save;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationObject
public class GameFileManager {
    @Inject private GameInfoFactory gameInfoFactory;
    @Inject private Game game;

    public List<GameSaveInfo> getSaves() {
        File gameDirectory = FileUtils.getSaveDirectory(game.getInfo().name);

        File[] files = gameDirectory.listFiles();
        return files != null ? Stream.of(files).map(this::getSaveInfo).filter(Objects::nonNull).collect(Collectors.toList()) : Collections.emptyList();
    }

    private GameSaveInfo getSaveInfo(File archiveFile) {
        try (InputStream archiveStream = new FileInputStream(archiveFile)) {
            try (ArchiveInputStream archive = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, archiveStream)) {
                ArchiveEntry entry;
                while ((entry = archive.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".json")) {
                        GameSaveInfo gameSaveInfo = GameSaveInfo.fromJSON(new JSONObject(IOUtils.toString(archive, StandardCharsets.UTF_8)));
                        gameSaveInfo.game = game.getInfo();
                        return gameSaveInfo;
                    }
                }
            }
        } catch (IOException | ArchiveException e) {
            Log.error(e);
        }
        return null;
    }

    public Texture getScreenshot(GameSaveInfo gameSaveInfo) {
        File gameDirectory = FileUtils.getSaveDirectory(gameSaveInfo.game.name);
        try (InputStream archiveStream = new FileInputStream(new File(gameDirectory, gameSaveInfo.filename + ".zip"))) {
            try (ArchiveInputStream archive = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, archiveStream)) {
                ArchiveEntry entry;
                while ((entry = archive.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".png")) {
                        Pixmap pixmap = new Pixmap(archive.readAllBytes(), 0, (int) archive.getBytesRead()); //error here
                        Texture t = new Texture(pixmap);
                        return t;
                    }
                }
            }
        } catch (IOException | ArchiveException e) {
            Log.error(e);
        }
        return null;
    }

    public List<GameInfo> buildGameList() {
        return FileUtils.list(FileUtils.getSaveDirectory()).stream()
                .filter(File::isDirectory)
                .map(gameDirectory -> {
                    File file = new File(gameDirectory, "game.json");
                    if (file.exists()) {
                        try {
                            Log.debug("Load game directory: " + gameDirectory.getName());
                            return gameInfoFactory.fromJSON(new JSONObject(Files.readString(file.toPath(), StandardCharsets.UTF_8)));
                        } catch (IOException e) {
                            Log.warning("Cannot load gameInfo for: " + file.getAbsolutePath());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void createSaveDirectory(GameInfo gameInfo) {
        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
        if (!gameDirectory.mkdirs()) {
            throw new GameException(GameFactory.class, "Unable to save repository: " + gameDirectory.getAbsolutePath());
        }
    }
}
