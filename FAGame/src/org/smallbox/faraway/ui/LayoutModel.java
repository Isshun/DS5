package org.smallbox.faraway.ui;

import java.util.List;

/**
 * Created by Alex on 28/05/2015.
 */
public class LayoutModel {
    public static class LayoutEntry {
        public String   type;
        public String   text;
        public String   id;
        public int      textsize;
        public int      textcolor;
        public int      background;
        public int[]    size;
        public int[]    position;
        public List<LayoutEntry> entries;
        public String   align;
    }

    public List<LayoutEntry> entries;
}
