package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.input.KeyModifier;
import org.smallbox.faraway.client.engine.animator.Animator;
import org.smallbox.faraway.client.engine.animator.CompositeAnimator;
import org.smallbox.faraway.client.engine.animator.IAnimator;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;

import java.util.function.BiConsumer;

@ApplicationObject
public class MenuRender {
    @Inject private MenuRenderInputProcessor menuRenderInputProcessor;
    @Inject private AssetManager assetManager;
    @Inject private UIRenderer UIRenderer;
    @Inject private UIManager uiManager;

    private Sprite backgroundSprite;
    private IAnimator backgroundAnimator;
    BiConsumer<Integer, KeyModifier> nextKeyConsumer;

    @OnInit
    public void init() {
        assetManager.load("data/background/17520.jpg", Texture.class);
        assetManager.finishLoading();

        backgroundSprite = new Sprite(assetManager.get("data/background/17520.jpg", Texture.class));
        backgroundSprite.flip(false, true);
        backgroundSprite.setScale(1.1f);
        backgroundSprite.setPosition(0, 0);

        backgroundAnimator = CompositeAnimator.of(
                new Animator(-50, 50, 0.0001f, Interpolation.pow2, (sprite, value) -> sprite.setPosition(value, value)),
                new Animator(1, 1.1f, 0.0005f, Interpolation.pow2, Sprite::setScale));
    }

    public void render() {
        Gdx.input.setInputProcessor(menuRenderInputProcessor);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        UIRenderer.clear();
        UIRenderer.refresh();

        if (backgroundSprite != null) {
            UIRenderer.drawSprite(backgroundAnimator.update(backgroundSprite));
        }

        uiManager.getMenuViews().forEach((name, view) -> view.draw(UIRenderer, 0, 0));
    }

    boolean clickOn(View view, int screenX, int screenY) {
        if (view.getEvents().hasClickListener() && view.isVisible() && hasHierarchyVisible(view) && view.contains(screenX, screenY)) {
            view.getEvents().click(screenX, screenY);
            return true;
        }

        if (view instanceof CompositeView && ((CompositeView)view).getViews() != null) {
            for (View subView: ((CompositeView)view).getViews()) {
                if (clickOn(subView, screenX, screenY)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasHierarchyVisible(View view) {
        if (view.getParent() != null) {
            return hasHierarchyVisible(view.getParent());
        }
        return view.isVisible();
    }

    public void getNextKey(BiConsumer<Integer, KeyModifier> consumer) {
        nextKeyConsumer = consumer;
    }

}
