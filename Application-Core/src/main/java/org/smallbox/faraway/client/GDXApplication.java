package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import org.smallbox.faraway.client.font.FontManager;
import org.smallbox.faraway.client.manager.BackgroundMusicManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.terrain.TerrainManager;
import org.smallbox.faraway.client.render.impl.MainRender;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ServerLuaModuleManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnApplicationLayerInit;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.task.TaskManager;

import java.time.LocalDateTime;

public class GDXApplication extends ApplicationAdapter {
    private MainRender mainRender;

    @Override
    public void create () {
        DependencyManager di = DependencyManager.getInstance();
        di.findAndCreateApplicationObjects();

        LocalDateTime localDateTime = LocalDateTime.now();

        TaskManager taskManager = di.getDependency(TaskManager.class);
        taskManager.addLoadTask("Calling dependency injector", false, di::injectApplicationDependencies);
        taskManager.addLoadTask("Generate fonts", true, () -> di.getDependency(FontManager.class).generateFonts());
        taskManager.addLoadTask("Create layer", true, () -> di.getDependency(GDXRenderer.class).init());
        taskManager.addLoadTask("Launch DB thread", false, () -> taskManager.launchBackgroundThread(di.getDependency(SQLManager.class)::update, 16));
        taskManager.addLoadTask("Load modules", false, () -> di.getDependency(ModuleManager.class).loadModules(null));
        taskManager.addLoadTask("Load server lua modules", false, () -> di.getDependency(ServerLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Load sprites", false, () -> di.getDependency(SpriteManager.class).init());
        taskManager.addLoadTask("Load terrains", false, () -> di.getDependency(TerrainManager.class).init());
        taskManager.addWaitTask("Loading sprites", false, () -> di.getDependency(AssetManager.class).update(16), () -> di.getDependency(AssetManager.class).getProgress());
        taskManager.addLoadTask("Load client lua modules", false, () -> di.getDependency(SpriteManager.class).setTexturesFilter());
        taskManager.addLoadTask("Load client lua modules", false, () -> di.getDependency(ClientLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Calling layer init", false, () -> di.callMethodAnnotatedBy(OnApplicationLayerInit.class));
        taskManager.addLoadTask("Calling layer init", false, () -> di.callMethodAnnotatedBy(AfterApplicationLayerInit.class));
        taskManager.addLoadTask("Background music", true, () -> di.getDependency(BackgroundMusicManager.class).start());
        taskManager.addLoadTask("Application ready", false, () -> Application.isLoaded = true);
        taskManager.addLoadTask("(debug) Resume game", false, GDXApplication::onCreateCompleted);

        mainRender = di.getDependency(MainRender.class);
    }

    private static void onCreateCompleted() {
        ApplicationConfig applicationConfig = DependencyManager.getInstance().getDependency(ApplicationConfig.class);

        if (applicationConfig.debug != null && applicationConfig.debug.actionOnLoad != null) {
            switch (applicationConfig.debug.actionOnLoad) {
                case LAST_SAVE:
                    DependencyManager.getInstance().getDependency(GameManager.class).loadLastGame();
                    break;
                case NEW_GAME:
                    GameFactory factory = DependencyManager.getInstance().getDependency(GameFactory.class);
                    factory.create(applicationConfig.debug.scenario);
                    break;
            }
        }
    }

    @Override
    public void render () {
        if (mainRender != null) {
            mainRender.render();
        }
    }

}