package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UIImage;

/**
 * Created by Alex on 02/06/2015.
 */
public class MainMenuPage extends UIFrame {
    protected final MainMenu.Scene  _scene;
    protected final MainMenu        _mainMenu;
    protected final GDXRenderer     _renderer;

    public MainMenuPage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene, String layoutPath) {
        super(renderer.getWidth(), renderer.getHeight());
        setPosition((renderer.getWidth() - Constant.BASE_WIDTH) / 2, (renderer.getHeight() - Constant.BASE_HEIGHT) / 2);
        _scene = scene;
        _mainMenu = mainMenu;
        _renderer = renderer;
    }

    public void init(ViewFactory viewFactory, UserInterface ui, UserInteraction interaction) {
        int width = 1920;
        int height = 1080;
        UIImage imageView = viewFactory.createImageView();
        imageView.setImage("data/planets/background.jpg");
        imageView.setPosition(-_x, -_y);
        imageView.setSize(width, height);
        imageView.setScale((double)_renderer.getWidth() / width, (double)_renderer.getHeight() / height);
        _views.add(0, imageView);
        imageView.setParent(this);
    }

    public MainMenu.Scene getSceneType() {
        return _scene;
    }

    public void open() {
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        return false;
    }
}
