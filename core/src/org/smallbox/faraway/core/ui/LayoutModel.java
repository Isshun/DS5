package org.smallbox.faraway.core.ui;

import java.util.List;

/**
 * Created by Alex on 28/05/2015.
 */
public class LayoutModel {
    public static class LayoutEntry {
        public String   type;
        public String   text;
        public String   id;
        public String   path;
        public String   style;
        public int      textsize;
        public int      textcolor;
        public long     background;
        public int[]    size;
        public int[]    position;
        public List<LayoutEntry> entries;
        public String   align;
        public String   file;
    }

    public List<LayoutEntry>    entries;
    public int                  background;
    public int[]                position;
    public int[]                size;
    public String[]             align;
    public String               id;
}
