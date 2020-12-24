package org.smallbox.faraway.client.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.smallbox.faraway.client.MenuManager;
import org.smallbox.faraway.client.controller.menu.MenuMainController;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.FileUtils;

@ApplicationObject
public class MenuRender {

    @Inject
    private GDXRenderer gdxRenderer;

    @Inject
    private MenuMainController menuMainController;

    private boolean _menuInit;
    private Texture _bgMenu;
    private Texture _bgMenu2;
    private BitmapFont _menuFont;

    private final InputProcessor _menuInputAdapter = new InputAdapter() {
        public boolean touchUp (int screenX, int screenY, int pointer, int button) {
            DependencyInjector.getInstance().getDependency(UIManager.class).getMenuViews().values().forEach(rootView -> clickOn(rootView.getView(), screenX, screenY));
            return false;
        }
    };

    public void render() {
        if (!_menuInit) {
            _menuInit = true;
            _bgMenu = new Texture(FileUtils.getFileHandle("data/graphics/menu_bg.jpg"));
            _bgMenu2 = new Texture(FileUtils.getFileHandle("data/graphics/menu_bg.png"));

            _menuFont = new BitmapFont(
                    new FileHandle(FileUtils.getDataFile("font-32.fnt")),
                    new FileHandle(FileUtils.getDataFile("font-32.png")),
                    false);
            _menuFont.setColor(new Color(0x80ced6ff));
        }

        Gdx.input.setInputProcessor(_menuInputAdapter);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        _batch.begin();
//
//        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        _batch.setProjectionMatrix(camera.combined);
//
//        _batch.draw(_bgMenu, 0, 0);
//        _batch.draw(_bgMenu2, 0, 0);
//
//        _menuFont.draw(_batch, "New Game", 32, Gdx.graphics.getHeight() - 32);
//        _menuFont.draw(_batch, "Exit", 32, Gdx.graphics.getHeight() - 80);
//

//        menuManager.display("base.ui.menu.main");


        // Render application
        gdxRenderer.clear();
        gdxRenderer.refresh();
        DependencyInjector.getInstance().getDependency(UIManager.class).getMenuViews().forEach((name, view) -> view.draw(gdxRenderer, 0, 0));
//
//        _batch.end();
    }

    private void clickOn(View view, int screenX, int screenY) {
        if (view.hasClickListener() && view.isVisible() && hasHeriarchieVisible(view)
                && screenX > view.getFinalX()
                && screenX < view.getFinalX() + view.getWidth()
                && screenY > view.getFinalY()
                && screenY < view.getFinalY() + view.getHeight()) {
            view.click(screenX, screenY);
        }

        if (view.getViews() != null) {
            view.getViews().forEach(subView -> clickOn(subView, screenX, screenY));
        }
    }

    private boolean hasHeriarchieVisible(View view) {
        if (view.getParent() != null) {
            return hasHeriarchieVisible(view.getParent());
        }
        return view.isVisible();
    }

}
