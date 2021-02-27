package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.asset.font.FontManager;
import org.smallbox.faraway.client.asset.music.BackgroundMusicManager;
import org.smallbox.faraway.client.asset.terrain.TerrainManager;
import org.smallbox.faraway.client.lua.ClientLuaModuleManager;
import org.smallbox.faraway.client.render.ErrorRender;
import org.smallbox.faraway.client.render.MainRender;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerComplete;
import org.smallbox.faraway.core.lua.ServerLuaModuleManager;
import org.smallbox.faraway.core.task.TaskManager;

public class GameApplication extends ApplicationAdapter {
    private final GDXApplicationListener listener;
    private final ErrorRender errorRender = new ErrorRender();
    private MainRender mainRender;

    public interface GDXApplicationListener {
        void onCreate();
    }

    public GameApplication(GDXApplicationListener listener) {
        this.listener = listener;
    }

    @Override
    public void create () {
        listener.onCreate();

        DependencyManager di = DependencyManager.getInstance();
        di.findAndCreateApplicationObjects();

        TaskManager taskManager = di.getDependency(TaskManager.class);
        taskManager.addLoadTask("Calling dependency injector", true, di::createApplicationDependencies);
        taskManager.addLoadTask("Generate fonts", true, () -> di.getDependency(FontManager.class).generateFonts());
        taskManager.addLoadTask("Load server lua modules", false, () -> di.getDependency(ServerLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Load client lua modules", false, () -> di.getDependency(ClientLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Init GDXRenderer", true, () -> di.getDependency(GDXRenderer.class).init());
        taskManager.addLoadTask("Init MapRenderer", true, () -> di.getDependency(MapRenderer.class).init());
        taskManager.addLoadTask("Init UIRenderer", true, () -> di.getDependency(UIRenderer.class).init());
        //taskManager.addLoadTask("Launch DB thread", true, () -> taskManager.launchBackgroundThread(di.getDependency(SQLManager.class)::update, 16));
//        taskManager.addLoadTask("Load modules", false, () -> di.getDependency(ModuleManager.class).loadModules(null));
        taskManager.addLoadTask("Load sprites", true, () -> di.getDependency(SpriteManager.class).init());
        taskManager.addLoadTask("Load terrains", true, () -> di.getDependency(TerrainManager.class).init());
        taskManager.addWaitTask("Loading sprites", true, () -> di.getDependency(AssetManager.class).update(16), di.getDependency(AssetManager.class));
        taskManager.addLoadTask("Set textures filter", true, () -> di.getDependency(SpriteManager.class).setTexturesFilter());
        taskManager.addLoadTask("Destroy useless controller", true, di::destroyNonBindControllers);
        taskManager.addLoadTask("Calling OnApplicationLayerInit", true, () -> di.getDependency(DependencyNotifier.class).notify(OnApplicationLayerBegin.class));
        taskManager.addLoadTask("Calling AfterApplicationLayerInit", true, () -> di.getDependency(DependencyNotifier.class).notify(OnApplicationLayerComplete.class));
        taskManager.addLoadTask("Background music", true, () -> di.getDependency(BackgroundMusicManager.class).start());
        taskManager.addLoadTask("Application ready", true, () -> Application.isLoaded = true);
        taskManager.addWaitTask("Waiting for background tasks", true, taskManager::allBackgroundTaskCompleted, null);
        taskManager.addLoadTask("Resolve futures", true, () -> di.getDependency(FontManager.class).resolveFutures());
        taskManager.addLoadTask("(debug) Resume game", true, GameApplication::onCreateCompleted);

        mainRender = di.getDependency(MainRender.class);
    }

    private static void onCreateCompleted() {
        ApplicationConfig applicationConfig = DependencyManager.getInstance().getDependency(ApplicationConfig.class);
//        DependencyManager.getInstance().getDependency(GameManager.class).loadLastGame();
//
//        if (applicationConfig.debug != null && applicationConfig.debug.actionOnLoad != null) {
//            switch (applicationConfig.debug.actionOnLoad) {
//                case CONTINUE -> DependencyManager.getInstance().getDependency(GameManager.class).loadLastGame();
//                case NEW_GAME -> DependencyManager.getInstance().getDependency(GameFactory.class).create(applicationConfig.debug.scenario);
//            }
//        }
    }

    @Override
    public void render () {
        if (mainRender != null) {
            mainRender.render();
        }
        errorRender.render();
    }

    @Override
    public void dispose() {
        DependencyManager.getInstance().getDependency(AssetManager.class).dispose();
        System.exit(0);
    }
}