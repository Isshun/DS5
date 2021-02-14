package org.smallbox.faraway.core.dependencyInjector;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
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
        DependencyManager.getInstance()._applicationObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass));
        DependencyManager.getInstance()._gameObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass));
    }

    <T> void callInitMethod(T model) {
        callMethodAnnotatedBy(model, OnInit.class);
    }

    <T> void callMethodAnnotatedBy(T model, Class<? extends Annotation> annotationClass) {
        for (Method method : model.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(annotationClass)) {
                if (shouldRunOnBackgroundThread(method, annotationClass)) {
                    DependencyManager.getInstance().getDependency(ThreadManager.class).addRunnable(invokeMethod(model, method));
                } else if (Gdx.app != null) {
                    Gdx.app.postRunnable(invokeMethod(model, method));
                } else {
                    invokeMethod(model, method).run();
                }
            }
        }
    }

    private boolean shouldRunOnBackgroundThread(Method method, Class<? extends Annotation> annotationClass) {
        return (annotationClass.equals(OnGameUpdate.class) && !method.getAnnotation(OnGameUpdate.class).runOnMainThread())
                || (annotationClass.equals(OnGameLongUpdate.class));
    }

    private <T> Runnable invokeMethod(T model, Method method) {
        return monitoringManager != null ? monitoringManager.encapsulateRunnable(model, buildRunnable(model, method)) : buildRunnable(model, method);
    }

    private <T> Runnable buildRunnable(T model, Method method) {
        return () -> {
            try {
                method.invoke(model);
            } catch (Exception e) {
                Log.error(e);
            }
        };
    }

}
