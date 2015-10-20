package org.smallbox.faraway.core.ui.mainMenu;

import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.ui.UserInteraction;
import org.smallbox.faraway.core.ui.UserInterface;
import org.smallbox.faraway.core.ui.engine.LayoutFactory;
import org.smallbox.faraway.core.ui.engine.ViewFactory;
import org.smallbox.faraway.core.ui.engine.views.UIImage;
import org.smallbox.faraway.core.ui.panel.BasePanel;
import org.smallbox.faraway.core.util.Constant;

/**
 * Created by Alex on 02/06/2015.
 */
public class MainMenuPage extends BasePanel {
    protected final MainMenu.Scene  _scene;
    protected final MainMenu        _mainMenu;
    protected final GDXRenderer     _renderer;

    public MainMenuPage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene, String layoutPath) {
        super(null, null, 0, 0, renderer.getWidth(), renderer.getHeight(), layoutPath);
        setPosition((renderer.getWidth() - Constant.BASE_WIDTH) / 2, (renderer.getHeight() - Constant.BASE_HEIGHT) / 2);
        _scene = scene;
        _mainMenu = mainMenu;
        _renderer = renderer;
    }

    @Override
    public void init(ViewFactory viewFactory, LayoutFactory layoutFactory, UserInterface ui, UserInteraction interaction) {
        super.init(viewFactory, layoutFactory, ui, interaction);

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

    @Override
    public void open() {
        onOpen();
    }
}
