package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.impl.ErrorRender;
import org.smallbox.faraway.client.render.impl.GameRender;
import org.smallbox.faraway.client.render.impl.MenuRender;
import org.smallbox.faraway.client.render.impl.MinimalRender;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ServerLuaModuleManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnApplicationLayerInit;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.groovy.GroovyManager;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.task.TaskManager;
import org.smallbox.faraway.util.FileUtils;

import java.io.File;

public class GDXApplication extends ApplicationAdapter {
    private final GameTestCallback _callback;
    private final TaskManager taskManager = new TaskManager();
    protected Application application;
    protected ApplicationClient client;
    protected SpriteBatch batch;
    protected BitmapFont[] fonts;
    private GameManager gameManager;
    private GameRender gameRender;
    private MenuRender menuRender;
    private MinimalRender minimalRender;
    private ErrorRender errorRender;

    public GDXApplication(GameTestCallback callback) {
        _callback = callback;
    }

    public interface GameTestCallback {
        void onApplicationReady();
    }

    @Override
    public void create () {
        DependencyInjector di = DependencyInjector.getInstance();

        batch = new SpriteBatch();
        application = new Application();

        taskManager.addLoadTask("Generate fonts", true, this::generateFonts);
        taskManager.addLoadTask("Create client app", false, () -> client = new ApplicationClient());
        taskManager.addLoadTask("Calling dependency injector", false, di::injectApplicationDependencies);
        taskManager.addLoadTask("Init groovy manager", false, di.getDependency(GroovyManager.class)::init);
        taskManager.addLoadTask("Create layer", true, () -> di.getDependency(GDXRenderer.class).init(batch, fonts));
        taskManager.addLoadTask("Launch DB thread", false, () -> taskManager.launchBackgroundThread(di.getDependency(SQLManager.class)::update, 16));
        taskManager.addLoadTask("Load modules", false, () -> di.getDependency(ModuleManager.class).loadModules(null));
        taskManager.addLoadTask("Load client lua modules", false, () -> di.getDependency(ClientLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Load server lua modules", false, () -> di.getDependency(ServerLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Load sprites", true, di.getDependency(SpriteManager.class)::init);
        taskManager.addLoadTask("Calling layer init", false, () -> di.callMethodAnnotatedBy(OnApplicationLayerInit.class));
        taskManager.addLoadTask("Calling layer init", false, () -> di.callMethodAnnotatedBy(AfterApplicationLayerInit.class));
        taskManager.addLoadTask("Resume game", false, this::resumeGame);

        gameManager = di.getDependency(GameManager.class);
        gameRender = di.getDependency(GameRender.class);
        menuRender = di.getDependency(MenuRender.class);
        minimalRender = di.getDependency(MinimalRender.class);
        errorRender = di.getDependency(ErrorRender.class);
    }

    private void generateFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        fonts = new BitmapFont[50];
        for (int i = 5; i < 50; i++) {
            fonts[i] = fontGen.createFont(new FileHandle(new File(FileUtils.BASE_PATH, "data/fonts/font.ttf")), "font-" + i, i);
            fonts[i].getData().flipped = true;
        }
    }

    private void resumeGame() {
        //            ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
//            Application.gameManager.loadLastGame();
//            Application.notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//            Application.gameManager.createGame(Application.data.getRegion("base.planet.corrin", "mountain"));
//                Application.gameManager.loadGame();

        if (_callback != null) {
            taskManager.addLoadTask("Test callback onApplicationReady", false, _callback::onApplicationReady);
        }

        Application.isLoaded = true;
    }

    @Override
    public void render () {
        if (gameManager.getGameStatus() == Game.GameStatus.STARTED) {
            gameRender.render();
        } else if (Application.isLoaded) {
            menuRender.render();
        } else {
            minimalRender.render(batch, taskManager);
        }

        errorRender.render(batch);
    }

    @Override
    public void dispose () {
//        Application.dependencyInjector.getObject(PathManager.class).close();
    }
}