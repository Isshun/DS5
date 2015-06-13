package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.ImageView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.panel.BasePanel;
import org.smallbox.faraway.ui.panel.LayoutFactory;

/**
 * Created by Alex on 02/06/2015.
 */
public class MainMenuScene extends BasePanel {
    protected final MainMenu.Scene  _scene;
    protected final MainMenu        _mainMenu;
    protected final GFXRenderer     _renderer;

    public MainMenuScene(MainMenu mainMenu, GFXRenderer renderer, MainMenu.Scene scene, String layoutPath) {
        super(null, null, 0, 0, renderer.getWidth(), renderer.getHeight(), layoutPath);
        setPosition((renderer.getWidth() - Constant.BASE_WIDTH) / 2, (renderer.getHeight() - Constant.BASE_HEIGHT) / 2);
        _scene = scene;
        _mainMenu = mainMenu;
        _renderer = renderer;
    }

    @Override
    public void init(ViewFactory viewFactory, LayoutFactory layoutFactory, UserInterface ui, UserInteraction interaction, RenderEffect effect) {
        super.init(viewFactory, layoutFactory, ui, interaction, effect);

        int width = 1920;
        int height = 1080;
        ImageView imageView = viewFactory.createImageView();
        imageView.setImagePath("data/planets/background.jpg");
        imageView.setPosition(-_x, -_y);
        imageView.setSize(width, height);
        imageView.setScale((double)_renderer.getWidth() / width, (double)_renderer.getHeight() / height);
        _views.add(0, imageView);
        imageView.setParent(this);
        imageView.resetPos();
    }

    public MainMenu.Scene getSceneType() {
        return _scene;
    }

    @Override
    public void open() {
        onOpen();
    }
}
