package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.asset.font.FontManager;
import org.smallbox.faraway.client.asset.music.BackgroundMusicManager;
import org.smallbox.faraway.client.asset.terrain.TerrainManager;
import org.smallbox.faraway.client.lua.ClientLuaModuleManager;
import org.smallbox.faraway.client.render.MainRender;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnApplicationLayerInit;
import org.smallbox.faraway.core.lua.ServerLuaModuleManager;
import org.smallbox.faraway.core.module.ModuleManager;
import org.smallbox.faraway.core.save.SQLManager;
import org.smallbox.faraway.core.task.TaskManager;

public class GameApplication extends ApplicationAdapter {
    private final GDXApplicationListener listener;
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
        taskManager.addLoadTask("Calling dependency injector", false, di::injectApplicationDependencies);
        taskManager.addLoadTask("Generate fonts", true, () -> di.getDependency(FontManager.class).generateFonts());
        taskManager.addLoadTask("Init GDXRenderer", true, () -> di.getDependency(GDXRenderer.class).init());
        taskManager.addLoadTask("Init MapRenderer", true, () -> di.getDependency(MapRenderer.class).init());
        taskManager.addLoadTask("Init UIRenderer", true, () -> di.getDependency(UIRenderer.class).init());
        taskManager.addLoadTask("Launch DB thread", false, () -> taskManager.launchBackgroundThread(di.getDependency(SQLManager.class)::update, 16));
        taskManager.addLoadTask("Load modules", false, () -> di.getDependency(ModuleManager.class).loadModules(null));
        taskManager.addLoadTask("Load server lua modules", false, () -> di.getDependency(ServerLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Load sprites", false, () -> di.getDependency(SpriteManager.class).init());
        taskManager.addLoadTask("Load terrains", false, () -> di.getDependency(TerrainManager.class).init());
        taskManager.addWaitTask("Loading sprites", false, () -> di.getDependency(AssetManager.class).update(16), () -> di.getDependency(AssetManager.class).getProgress());
        taskManager.addLoadTask("Set textures filter", false, () -> di.getDependency(SpriteManager.class).setTexturesFilter());
        taskManager.addLoadTask("Load client lua modules", false, () -> di.getDependency(ClientLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Destroy useless controller", false, di::destroyNonBindControllers);
        taskManager.addLoadTask("Calling OnApplicationLayerInit", false, () -> di.callMethodAnnotatedBy(OnApplicationLayerInit.class));
        taskManager.addLoadTask("Calling AfterApplicationLayerInit", false, () -> di.callMethodAnnotatedBy(AfterApplicationLayerInit.class));
        taskManager.addLoadTask("Background music", true, () -> di.getDependency(BackgroundMusicManager.class).start());
        taskManager.addLoadTask("Application ready", false, () -> Application.isLoaded = true);
        taskManager.addLoadTask("(debug) Resume game", false, GameApplication::onCreateCompleted);

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
    }

    @Override
    public void dispose() {
        DependencyManager.getInstance().getDependency(AssetManager.class).dispose();
        System.exit(0);
    }
}