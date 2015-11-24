package org.smallbox.faraway.core.util;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02/07/2015.
 */
public class FileUtils {
    public static void write(File file, String str) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(str.getBytes("UTF-8"));
        fos.close();
    }

    public static List<File> listRecursively(String filePath) {
        List<File> list = new ArrayList<>();
        listDirectory(list, new File(filePath), true);
        return list;
    }

    public static List<File> list(File file) {
        List<File> list = new ArrayList<>();
        listDirectory(list, file, false);
        return list;
    }

    public static List<File> list(String filePath) {
        List<File> list = new ArrayList<>();
        listDirectory(list, new File(filePath), false);
        return list;
    }

    private static void listDirectory(List<File> list, File directory, boolean recursively) {
        for (File file: directory.listFiles()) {
            list.add(file);
            if (file.isDirectory()) {
                if (recursively) {
                    listDirectory(list, file, recursively);
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
            try {
                FileInputStream fis = new FileInputStream("data/config.json");
                FileOutputStream fos = new FileOutputStream(configFile);
                IOUtils.copy(fis, fos);
                fis.close();
                fos.close();
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
}
