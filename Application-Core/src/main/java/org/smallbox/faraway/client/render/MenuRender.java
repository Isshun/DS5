package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.input.GameEventListener;
import org.smallbox.faraway.client.layer.Animator;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.client.ui.RootView;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

import java.util.function.BiConsumer;

@ApplicationObject
public class MenuRender {
    @Inject private UIRenderer UIRenderer;
    @Inject private UIEventManager uiEventManager;
    @Inject private ShortcutManager shortcutManager;
    @Inject private UIManager uiManager;
    @Inject private AssetManager assetManager;

    private Sprite backgroundSprite;
    private Animator backgroundAnimator1;
    private Animator backgroundAnimator2;
    private Animator backgroundAnimator3;
    private BiConsumer<Integer, GameEventListener.Modifier> nextKeyConsumer;

    private final InputProcessor _menuInputAdapter = new InputAdapter() {

        @Override
        public boolean touchUp (int screenX, int screenY, int pointer, int button) {
            for (RootView rootView: uiManager.getMenuViews().values()) {
                if (clickOn(rootView.getView(), screenX, screenY)) {
                    return true;
                }
            }
            uiEventManager.onMouseRelease(screenX, screenY, button);
            return false;
        }

        @Override
        public boolean keyUp (int keycode) {
            if (nextKeyConsumer != null) {
                nextKeyConsumer.accept(keycode, null);
                return false;
            }

            shortcutManager.action(keycode);
            return false;
        }

        @Override
        public boolean mouseMoved(int x, int y) {
            uiEventManager.onMouseMove(x, y, false);
            return false;
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            uiEventManager.onMousePress(x, y, button);
            return false;
        }

        @Override
        public boolean touchDragged(int x, int y, int pointer) {
            uiEventManager.onDrag(x, y, false);
            return false;
        }

    };

    @OnInit
    public void init() {
        assetManager.load("data/background/17520.jpg", Texture.class);
        assetManager.finishLoading();

        backgroundSprite = new Sprite(assetManager.get("data/background/17520.jpg", Texture.class));
        backgroundSprite.flip(false, true);
        backgroundSprite.setScale(1.1f);
        backgroundSprite.setPosition(0, 0);

        backgroundAnimator1 = new Animator(-50, 50, 0.0001f, Interpolation.pow2, (sprite, value) -> {
            sprite.setPosition(value, value);
        });

        backgroundAnimator2 = new Animator(0, 5f, 0.0001f, Interpolation.pow2, Sprite::setRotation);
        backgroundAnimator3 = new Animator(1, 1.1f, 0.0005f, Interpolation.pow2, Sprite::setScale);
    }

    public void render() {
        Gdx.input.setInputProcessor(_menuInputAdapter);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        UIRenderer.clear();
        UIRenderer.refresh();

        if (backgroundSprite != null) {
            backgroundAnimator1.update(backgroundSprite);
//            backgroundAnimator2.update(backgroundSprite);
            backgroundAnimator3.update(backgroundSprite);
            UIRenderer.drawSprite(backgroundSprite);
//            backgroundSprite.scale(0.00001f);
//            backgroundSprite.translate(0.02f, 0.035f);
        }

        uiManager.getMenuViews().forEach((name, view) -> view.draw(UIRenderer, 0, 0));
    }

    private boolean clickOn(View view, int screenX, int screenY) {
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

    public void getNextKey(BiConsumer<Integer, GameEventListener.Modifier> consumer) {
        nextKeyConsumer = consumer;
    }
}
