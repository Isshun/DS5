package org.smallbox.faraway.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static void write(FileOutputStream fos, String str) throws IOException {
        fos.write(str.getBytes("UTF-8"));
        fos.write('\n');
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
}
