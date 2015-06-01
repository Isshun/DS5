import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.smallbox.faraway.Application;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.PathManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;

import java.io.IOException;

/**
 * Created by Alex on 27/05/2015.
 */
public class Main {
    private static Game _game;

    public static void main(String[] args) throws IOException {
        ViewFactory.setInstance(new SFMLViewFactory());

        final RenderWindow window = new RenderWindow();
        window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "FarPoint", WindowStyle.DEFAULT);
        window.setKeyRepeatEnabled(true);

        SpriteManager.setInstance(new SFMLSpriteManager());

        SFMLRenderer renderer = new SFMLRenderer(window);

        Application application = new Application(renderer);

        // Load resources
        application.getLoadListener().onUpdate("Init resources");
        GameData data = application.loadResources();

        // Create app
        application.create(renderer);
        renderer.setGameEventListener(application);

        try {
            _game = new Game(data);
            _game.onCreate();
            //_game.newGame(SAVE_FILE, _loadListener);

            application.getLoadListener().onUpdate("Load save");
            _game.load(Application.SAVE_FILE, application.getLoadListener());

            application.getLoadListener().onUpdate("Start game");
            application.startGame(_game);

            loop(window, application, renderer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //		//Limit the framerate
        //		window.setFramerateLimit(30);

        renderer.close();
        PathManager.getInstance().close();
    }

    private static void loop(RenderWindow window, Application application, final GFXRenderer renderer) throws IOException, InterruptedException {
        long renderTime = 0;

        int update = 0;
        int refresh = 0;
        int frame = 0;
        long nextDraw = 0;
        long nextUpdate = 0;
        long nextRefresh = 0;
        long nextLongUpdate = 0;

        while (window.isOpen()) {
            renderer.refresh();

            long elapsed = renderer.getTimer().getElapsedTime();

            // Sleep
            if (elapsed < nextDraw) {
                //int currentRenderTime = (int) (DRAW_INTERVAL - (nextDraw - elapsed));
                //renderTime = (renderTime * 7 + currentRenderTime) / 8;
                Thread.sleep(nextDraw - elapsed);
            }

            // Render menu
            if (!_game.isRunning()) {
                application.manageMenu(renderer);
            }

            // Render game
            else if (!_game.isPaused()) {
                // Draw
                RenderEffect effect = SpriteManager.getInstance().createRenderEffect();
                effect.setViewport(_game.getViewport());

                double animProgress = (1 - (double) (nextUpdate - elapsed) / Application.getUpdateInterval());
                application.render(animProgress, update, renderTime, renderer, effect);

                    // Refresh
                    if (elapsed >= nextRefresh) {
                        application.refresh(refresh);
                        refresh++;
                        nextRefresh += Application.REFRESH_INTERVAL;
                    }

                    // Update
                    if (elapsed >= nextUpdate) {
                        application.update(update);
                        update++;
                        nextUpdate += Application.getUpdateInterval();
                    }

                    // Long update
                    if (elapsed >= nextLongUpdate) {
                        application.longUpdate(frame);
                        nextLongUpdate += Application.getLongUpdateInterval();
                    }
            }

            // Draw
            renderer.display();
            nextDraw += Application.DRAW_INTERVAL;
            frame++;
        }
    }
}