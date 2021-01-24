package org.smallbox.faraway.core.save;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.util.FileUtils;
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
                        return GameSaveInfo.fromJSON(new JSONObject(IOUtils.toString(archive, StandardCharsets.UTF_8)));
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
                            Log.info("Load game directory: " + gameDirectory.getName());
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

}
