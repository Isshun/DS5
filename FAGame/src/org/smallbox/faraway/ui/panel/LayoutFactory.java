package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.ui.LayoutModel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Alex on 28/05/2015.
 */
public class LayoutFactory {
    public interface OnLayoutLoaded {
        void onLayoutLoaded(LayoutModel layout);
    }

    public static void load(String path, BasePanel panel, OnLayoutLoaded listener) {
        try {
            InputStream input = new FileInputStream(new File(path));
            Yaml yaml = new Yaml(new Constructor(LayoutModel.class));
            LayoutModel layout = (LayoutModel)yaml.load(input);
            if (layout.entries != null && !layout.entries.isEmpty()) {
                for (LayoutModel.LayoutEntry entry : layout.entries) {
                    panel.addView(createFromLayout(entry));
                }
            }

            panel.setLoaded();
            panel.resetAllPos();

            listener.onLayoutLoaded(layout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static View createLabel(LayoutModel.LayoutEntry entry) {
        TextView lbText = ViewFactory.getInstance().createTextView();

        if (entry.textsize != 0) {
            lbText.setCharacterSize(entry.textsize);
        }

        if (entry.textcolor != 0) {
            lbText.setColor(new Color(entry.textcolor));
        }
        lbText.setString(entry.text);

        lbText.setSize(lbText.getContentWidth() + 10, lbText.getContentHeight() + 10);

        if (entry.size != null) {
            lbText.setSize(entry.size[0], entry.size[1]);
        }

        if (entry.position != null) {
            lbText.setPosition(entry.position[0], entry.position[1]);
        }

        lbText.init();

        return lbText;
    }

    private static View createFrame(LayoutModel.LayoutEntry entry) {
        FrameLayout frame = ViewFactory.getInstance().createFrameLayout();

        if (entry.position != null) {
            frame.setPosition(entry.position[0], entry.position[1]);
        }

        if (entry.size != null) {
            frame.setPosition(
                    entry.size[0] != -1 ? entry.size[0] : Application.getWindowWidth(),
                    entry.size[1] != -1 ? entry.size[1] : Application.getWindowHeight());
        }

        if (entry.entries != null) {
            for (LayoutModel.LayoutEntry subEntry : entry.entries) {
                frame.addView(createFromLayout(subEntry));
            }
        }

        return frame;
    }

    public static View createFromLayout(LayoutModel.LayoutEntry entry) {
        View view = null;

        switch (entry.type) {
            case "label":
                view = createLabel(entry);
                break;
            case "frame":
                view = createFrame(entry);
        }

        if (view != null && entry.id != null) {
            view.setId(entry.id.hashCode());
        }

        return view;
    }
}
