import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;

import java.io.IOException;

/**
 * Created by Alex on 27/05/2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        ViewFactory.setInstance(new SFMLViewFactory());

        final RenderWindow window = new RenderWindow();
        window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "FarPoint", WindowStyle.DEFAULT);
        window.setKeyRepeatEnabled(true);

        SpriteManager.setInstance(new SFMLSpriteManager());

        SFMLRenderer renderer = new SFMLRenderer(window);
        org.smallbox.faraway.Main main = new org.smallbox.faraway.Main();
        renderer.setGameEventListener(main);
        main.create(renderer);
    }
}