package org.smallbox.faraway.core.ui.engine;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.ui.LayoutModel;
import org.smallbox.faraway.core.ui.engine.views.UIFrame;
import org.smallbox.faraway.core.ui.engine.views.UIImage;
import org.smallbox.faraway.core.ui.engine.views.UILabel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class ViewFactory {
    public interface ViewFactoryLoadListener {
        void onLoad(UIFrame rootView);
    }

    private static ViewFactory  _factory;

    public static void          setInstance(ViewFactory factory) {
        _factory = factory;
    }

    public static ViewFactory   getInstance() {
        return _factory;
    }

    public abstract UILabel     createTextView();
    public abstract UILabel     createTextView(int width, int height);
    public abstract UIFrame createFrameLayout();
    public abstract UIFrame createFrameLayout(int width, int height);
    public abstract UIImage createImageView();

    public void load(String path, ViewFactoryLoadListener listener) {
        UIFrame rootView = load(path);
        if (listener != null) {
            listener.onLoad(rootView);
        }
    }

    public UIFrame load(String path) {
        try {
            UIFrame rootView = createFrameLayout();
            InputStream input = new FileInputStream(new File(path));
            Yaml yaml = new Yaml(new Constructor(LayoutModel.class));
            LayoutModel layout = (LayoutModel)yaml.load(input);
            if (layout.entries != null && !layout.entries.isEmpty()) {
                for (LayoutModel.LayoutEntry entry : layout.entries) {
                    rootView.addView(LayoutFactory.createFromLayout(null, entry));
                }
            }

            if (layout.id != null) {
                rootView.setId(layout.id);
            }

            if (layout.align != null) {
                rootView.setTextAlign("left".equals(layout.align[0]), "top".equals(layout.align[1]));
            }

            if (layout.position != null) {
                rootView.setPosition(layout.position[0], layout.position[1]);
            }

            if (layout.size != null) {
                rootView.setSize(layout.size[0], layout.size[1]);
            }

            if (layout.background != 0) {
                rootView.setBackgroundColor(new Color(layout.background));
            }

            return rootView;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
