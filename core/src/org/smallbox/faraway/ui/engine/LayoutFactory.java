package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UIImage;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.ui.engine.views.View;
import org.smallbox.faraway.ui.panel.BasePanel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Alex on 28/05/2015.
 */
public class LayoutFactory {
    public interface OnLayoutLoaded {
        void onLayoutLoaded(LayoutModel layout, UIFrame panel);
    }

    public static void load(String path, OnLayoutLoaded listener) {
        UIFrame panel = new UIFrame(100, 100);
        load(path, panel, listener);
    }

    public static void load(String path, UIFrame panel, OnLayoutLoaded listener) {
        try {
            InputStream input = new FileInputStream(new File(path.endsWith(".yml") ? path : path + ".yml"));
            Yaml yaml = new Yaml(new Constructor(LayoutModel.class));
            LayoutModel layout = (LayoutModel)yaml.load(input);
            if (layout.entries != null && !layout.entries.isEmpty()) {
                for (LayoutModel.LayoutEntry entry : layout.entries) {
                    panel.addView(createFromLayout(panel, entry));
                }
            }

            if (layout.id != null) {
                panel.setId(layout.id.hashCode());
            }

            if (layout.align != null) {
                panel.setTextAlign("left".equals(layout.align[0]), "top".equals(layout.align[1]));
            }

            if (layout.position != null) {
                panel.setPosition(layout.position[0], layout.position[1]);
            }

            if (layout.size != null) {
                panel.setSize(layout.size[0], layout.size[1]);
            }

            if (layout.background != 0) {
                panel.setBackgroundColor(new Color(layout.background));
            }

            panel.init();
            if (panel instanceof BasePanel) {
                ((BasePanel)panel).setLoaded();
            }

            if (listener != null) {
                listener.onLayoutLoaded(layout, panel);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static View createLabel(View parent, LayoutModel.LayoutEntry entry) {
        UILabel lbText = ViewFactory.getInstance().createTextView();

        if (entry.align != null) {
            switch (entry.align) {
                case "center": lbText.setTextAlign(UILabel.Align.CENTER); break;
                case "center_vertical": lbText.setTextAlign(UILabel.Align.CENTER_VERTICAL); break;
            }
        }

        if (entry.style != null) {
            switch (entry.style) {
                case "bold": lbText.setStyle(UILabel.BOLD); break;
            }
        }

        if (entry.textsize != 0) {
            lbText.setTextSize(entry.textsize);
        }

        if (entry.textcolor != 0) {
            lbText.setTextColor(new Color(entry.textcolor));
        }

        if (entry.text != null) {
            lbText.setText(entry.text);
        }

//        lbText.setSize(lbText.getContentWidth() + 10, lbText.getContentHeight() + 10);

        if (entry.size != null) {
            lbText.setSize(
                    entry.size[0] != -1 ? entry.size[0] : parent != null ? parent.getContentWidth() : 0,
                    entry.size[1] != -1 ? entry.size[1] : parent != null ? parent.getContentHeight() : 0);
        }

        if (entry.position != null) {
            lbText.setPosition(entry.position[0], entry.position[1]);
        }

        if (entry.background != 0) {
            lbText.setBackgroundColor(new Color(entry.background));
        }

        lbText.init();

        return lbText;
    }

    private static View createFrame(View parent, LayoutModel.LayoutEntry entry) {
        UIFrame frame = ViewFactory.getInstance().createFrameLayout();

        if (entry.align != null) {
            switch (entry.align) {
                case "center": frame.setTextAlign(UILabel.Align.CENTER); break;
            }
        }

        if (entry.position != null) {
            frame.setPosition(entry.position[0], entry.position[1]);
        }

        if (entry.size != null) {
            frame.setSize(
                    entry.size[0] != -1 ? entry.size[0] : parent != null ? parent.getContentWidth() : 0,
                    entry.size[1] != -1 ? entry.size[1] : parent != null ? parent.getContentHeight() : 0);
        }

        if (entry.entries != null) {
            for (LayoutModel.LayoutEntry subEntry : entry.entries) {
                frame.addView(createFromLayout(frame, subEntry));
            }
        }

        if (entry.background != 0) {
            frame.setBackgroundColor(new Color(entry.background));
        }

        return frame;
    }

    private static View createImage(View parent, LayoutModel.LayoutEntry entry) {
        UIImage imageView = ViewFactory.getInstance().createImageView();

        if (entry.position != null) {
            imageView.setPosition(entry.position[0], entry.position[1]);
        }

        if (entry.size != null) {
            imageView.setSize(
                    entry.size[0] != -1 ? entry.size[0] : parent != null ? parent.getContentWidth() : 0,
                    entry.size[1] != -1 ? entry.size[1] : parent != null ? parent.getContentHeight() : 0);
        }

        if (entry.path != null) {
            imageView.setImage(entry.path);
        }

        return imageView;
    }

    public static View createFromLayout(View parent, LayoutModel.LayoutEntry entry) {
        View view = null;

        switch (entry.type) {
            case "include":
                view = ViewFactory.getInstance().load(entry.file);
                break;
            case "label":
                view = createLabel(parent, entry);
                break;
            case "image":
                view = createImage(parent, entry);
                break;
            case "frame":
                view = createFrame(parent, entry);
                break;
        }

        if (view != null && entry.id != null) {
            view.setId(entry.id);
        }

        return view;
    }
}
