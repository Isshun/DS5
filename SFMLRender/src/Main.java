import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.model.GameData;

import java.io.IOException;

/**
 * Created by Alex on 27/05/2015.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ViewFactory.setInstance(new SFMLViewFactory());

        final RenderWindow window = new RenderWindow();
        window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), Constant.NAME + " " + Constant.VERSION, WindowStyle.DEFAULT);
        window.setKeyRepeatEnabled(true);

        SpriteManager.setInstance(new SFMLSpriteManager());

        SFMLRenderer renderer = new SFMLRenderer(window);

        Application application = new Application(renderer);

        // Load resources
        application.getLoadListener().onUpdate("Init resources");
        GameData data = application.loadResources();

        // Create app
//        application.create(renderer, data);
        renderer.setGameEventListener(application);

        try {
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

            Game game = application.getGame();

            long elapsed = renderer.getTimer().getElapsedTime();

            // Sleep
            if (elapsed < nextDraw) {
                //int currentRenderTime = (int) (DRAW_INTERVAL - (nextDraw - elapsed));
                //renderTime = (renderTime * 7 + currentRenderTime) / 8;
                Thread.sleep(nextDraw - elapsed);
            }

            // Render menu
            if (game == null || !game.isRunning()) {
                application.renderMenu(renderer, SpriteManager.getInstance().createRenderEffect());

                // Refresh
                if (elapsed >= nextRefresh) {
                    application.refreshMenu(refresh++);
                    nextRefresh += Application.REFRESH_INTERVAL;
                }
            }

            // Render game
            else if (game != null && !game.isPaused()) {
                // Draw
                RenderEffect effect = SpriteManager.getInstance().createRenderEffect();
                effect.setViewport(game.getViewport());

                double animProgress = (1 - (double) (nextUpdate - elapsed) / Application.getUpdateInterval());
                application.renderGame(animProgress, update, renderTime, renderer, effect);

                    // Refresh
                    if (elapsed >= nextRefresh) {
                        application.refreshGame(refresh++);
                        nextRefresh += Application.REFRESH_INTERVAL;
                    }

                    // Update
                    if (elapsed >= nextUpdate) {
                        application.update(update++);
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