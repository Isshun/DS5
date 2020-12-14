package org.smallbox.faraway.util;

import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.smallbox.faraway.core.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02/07/2015.
 */
public class FileUtils {

    public static void write(File file, String str) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(str.getBytes("UTF-8"));
        }
    }

    public static List<File> listRecursively(String relativePath) {
        return listRecursively(new File(Application.BASE_PATH, relativePath));
    }

    public static List<File> listRecursively(File file) {
        List<File> list = new ArrayList<>();
        listDirectory(list, file, true);
        return list;
    }

    public static List<File> list(File file) {
        List<File> list = new ArrayList<>();
        listDirectory(list, file, false);
        return list;
    }

    private static void listDirectory(List<File> list, File directory, boolean isRecursive) {
        for (File file: directory.listFiles()) {
            list.add(file);
            if (file.isDirectory()) {
                if (isRecursive) {
                    listDirectory(list, file, isRecursive);
                }
            }
        }
    }

    public static String read(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    public static void createRoamingDirectory() {
        File gameDirectory = getGameDirectory();
        if (!gameDirectory.exists()) {
            gameDirectory.mkdir();
        }

        File saveDirectory = new File(gameDirectory, "saves");
        if (!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }

        File configFile = new File(gameDirectory, "config.json");
        if (!configFile.exists()) {
            try (FileInputStream fis = new FileInputStream("data/config.json")) {
                try (FileOutputStream fos = new FileOutputStream(configFile)) {
                    IOUtils.copy(fis, fos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getGameDirectory() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new File(System.getenv("AppData"), "FarAway");
        }
        if (SystemUtils.IS_OS_MAC_OSX) {
            return new File(System.getProperty("user.home") + "/Library/Application Support", "FarAway");
        }
        if (SystemUtils.IS_OS_LINUX) {
            return new File(System.getProperty("user.home"), ".faraway");
        }
        throw new RuntimeException("Non-compatible OS");
    }

    public static File getSaveDirectory() {
        return new File(getGameDirectory(), "saves");
    }

    public static File getSaveDirectory(String directory) {
        return new File(new File(getGameDirectory(), "saves"), directory);
    }

    public static File getFile(String... relativePath) {
        return Paths.get(Application.BASE_PATH, relativePath).toFile();
    }

    public static FileHandle getFileHandle(String relativePath) {
        return new FileHandle(getFile(relativePath));
    }
}
