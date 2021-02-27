package org.smallbox.faraway.core.dependencyInjector;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.MonitoringManager;
import org.smallbox.faraway.core.game.ThreadManager;
import org.smallbox.faraway.util.log.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@ApplicationObject
public class DependencyNotifier {
    @Inject private MonitoringManager monitoringManager;

    public void notify(Class<? extends Annotation> annotationClass) {
        DependencyManager.getInstance()._applicationObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass, null));
        DependencyManager.getInstance()._gameObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass, null));
    }

    public <T_PARAM> void notify(Class<? extends Annotation> annotationClass, T_PARAM param) {
        DependencyManager.getInstance()._applicationObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass, param));
        DependencyManager.getInstance()._gameObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass, param));
    }

    <T, T_PARAM> void callMethodAnnotatedBy(T model, Class<? extends Annotation> annotationClass, T_PARAM param) {
        for (Method method: model.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(annotationClass)) {
                if (shouldRunOnBackgroundThread(method, annotationClass)) {
                    DependencyManager.getInstance().getDependency(ThreadManager.class).addRunnable(invokeMethod(model, method, param));
                } else if (Gdx.app != null) {
                    Gdx.app.postRunnable(invokeMethod(model, method, param));
                } else {
                    invokeMethod(model, method, param).run();
                }
            }
        }
    }

    private boolean shouldRunOnBackgroundThread(Method method, Class<? extends Annotation> annotationClass) {
        return (annotationClass.equals(OnGameUpdate.class) && !method.getAnnotation(OnGameUpdate.class).runOnMainThread())
                || (annotationClass.equals(OnGameLongUpdate.class));
    }

    private <T, T_PARAM> Runnable invokeMethod(T model, Method method, T_PARAM param) {
        return monitoringManager != null ? monitoringManager.encapsulateRunnable(model, buildRunnable(model, method, param)) : buildRunnable(model, method, param);
    }

    private <T, T_PARAM> Runnable buildRunnable(T model, Method method, T_PARAM param) {
        return () -> {
            try {
                if (method.getParameterCount() == 1) {
                    method.invoke(model, param);
                } else {
                    method.invoke(model);
                }
            } catch (Exception e) {
                Log.error(e);
            }
        };
    }

}
