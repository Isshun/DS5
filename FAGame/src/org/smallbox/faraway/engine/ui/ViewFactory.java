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
        void onLoad(View view);
    }

    private static ViewFactory _factory;

    public static void setInstance(ViewFactory factory) {
        _factory = factory;
    }

    public static ViewFactory getInstance() {
        return _factory;
    }

    public abstract TextView createTextView();
    public abstract TextView createTextView(int width, int height);
    public abstract ColorView createColorView();
    public abstract ColorView createColorView(int width, int height);
    public abstract FrameLayout createFrameLayout();
    public abstract FrameLayout createFrameLayout(int width, int height);
    public abstract ImageView createImageView();
    public abstract RenderLayer createRenderLayer(int width, int height);

    public void load(String path, ViewFactoryLoadListener listener) {
        try {
            FrameLayout root = createFrameLayout();
            InputStream input = new FileInputStream(new File(path));
            Yaml yaml = new Yaml(new Constructor(LayoutModel.class));
            LayoutModel layout = (LayoutModel)yaml.load(input);
            if (layout.entries != null && !layout.entries.isEmpty()) {
                for (LayoutModel.LayoutEntry entry : layout.entries) {
                    View view = LayoutFactory.createFromLayout(null, entry);
                    root.addView(view);
                }
            }
            root.resetAllPos();
            listener.onLoad(root);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
