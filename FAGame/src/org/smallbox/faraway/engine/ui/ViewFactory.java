package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.panel.LayoutFactory;
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
        void onLoad(FrameLayout rootView);
    }

    private static ViewFactory  _factory;

    public static void          setInstance(ViewFactory factory) {
        _factory = factory;
    }

    public static ViewFactory   getInstance() {
        return _factory;
    }

    public abstract TextView    createTextView();
    public abstract TextView    createTextView(int width, int height);
    public abstract ColorView   createColorView();
    public abstract ColorView   createColorView(int width, int height);
    public abstract FrameLayout createFrameLayout();
    public abstract FrameLayout createFrameLayout(int width, int height);
    public abstract ImageView   createImageView();
    public abstract ImageView   createImageView(int width, int height);
    public abstract RenderLayer createRenderLayer(int width, int height);

    public void load(String path, ViewFactoryLoadListener listener) {
        try {
            FrameLayout rootView = createFrameLayout();
            InputStream input = new FileInputStream(new File(path));
            Yaml yaml = new Yaml(new Constructor(LayoutModel.class));
            LayoutModel layout = (LayoutModel)yaml.load(input);
            if (layout.entries != null && !layout.entries.isEmpty()) {
                for (LayoutModel.LayoutEntry entry : layout.entries) {
                    rootView.addView(LayoutFactory.createFromLayout(null, entry));
                }
            }
            rootView.resetAllPos();
            listener.onLoad(rootView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
