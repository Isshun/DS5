package org.smallbox.faraway.util;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Alex on 02/07/2015.
 */
public class FileUtils {
    public static void write(FileOutputStream fos, String str) throws IOException {
        fos.write(str.getBytes("UTF-8"));
        fos.write('\n');
    }
}
