package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.ui.OnFocusListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
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
            for (LayoutModel.LayoutEntry entry: layout.entries) {
                panel.addView(createFromLayout(entry));
            }
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

        lbText.setPosition(entry.position[0], entry.position[1]);

        lbText.init();

        return lbText;
    }

    public static View createFromLayout(LayoutModel.LayoutEntry entry) {
        View view = null;

        switch (entry.type) {
            case "label":
                view = createLabel(entry);
                break;
        }

        if (view != null && entry.id != null) {
            view.setId(entry.id.hashCode());
        }

        return view;
    }
}
