//package org.smallbox.faraway.ui.mainMenu;
//
//import org.smallbox.faraway.core.engine.GameEventListener;
//import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
//import org.smallbox.faraway.core.util.Constant;
//import org.smallbox.faraway.ui.GameActionExtra;
//import org.smallbox.faraway.ui.UserInterface;
//import org.smallbox.faraway.ui.engine.views.widgets.UIFrame;
//import org.smallbox.faraway.ui.engine.views.widgets.UIImage;
//
///**
// * Created by Alex on 02/06/2015.
// */
//public class MainMenuPage extends UIFrame {
//    protected final MainMenu.Scene  _scene;
//    protected final MainMenu        _mainMenu;
//    protected final GDXRenderer     _renderer;
//
//    public MainMenuPage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene, String layoutPath) {
//        super(renderer.getWidth(), renderer.getHeight());
//        setPosition((renderer.getWidth() - Constant.BASE_WIDTH) / 2, (renderer.getHeight() - Constant.BASE_HEIGHT) / 2);
//        _scene = scene;
//        _mainMenu = mainMenu;
//        _renderer = renderer;
//    }
//
//    public void init(UserInterface ui, GameActionExtra interaction) {
//        int width = 1920;
//        int height = 1080;
//        UIImage imageView = new UIImage(width, height);
//        imageView.setImage("data/planets/background.jpg");
//        imageView.setPosition(-_x, -_y);
//        imageView.setSize(width, height);
//        imageView.setScale((double)_renderer.getWidth() / width, (double)_renderer.getHeight() / height);
//        _views.add(0, imageView);
//        imageView.setParent(this);
//    }
//
//    public MainMenu.Scene getSceneType() {
//        return _scene;
//    }
//
//    public void open() {
//    }
//
//    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
//        return false;
//    }
//}
