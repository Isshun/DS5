package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.module.java.ModuleManager;

import java.util.List;

public class MainRenderer {
    public static final int             WORLD_RENDERER_LEVEL = -100;
    public static final int             PARTICLE_RENDERER_LEVEL = -99;
    public static final int             INNER_RENDERER_LEVEL = -98;
    public static final int             CHARACTER_RENDERER_LEVEL = -97;
    public static final int             JOB_RENDERER_LEVEL = -96;

    private static MainRenderer         _self;
    private static long                 _renderTime;
    private static int                  _frame;
    private final List<BaseRenderer>    _renders;
    private SpriteManager               _spriteManager;
    private CharacterRenderer           _characterRenderer;
    private WorldRenderer               _worldRenderer;

    public MainRenderer(GDXRenderer renderer, GameConfig config) {
        _self = this;
        _spriteManager = SpriteManager.getInstance();
        _renders = ModuleManager.getInstance().getRenders();

    }

    public void onRefresh(int frame) {
        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }
    }

    public void onUpdate() {
        _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::update);
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        long time = System.currentTimeMillis();

        String display = Game.getInstance().getDisplay();
        _renders.stream().filter(BaseRenderer::isLoaded)
                .filter(render -> !(render instanceof GameDisplay) || (display != null && display.equals(((GameDisplay)render).getName())))
                .forEach(render -> render.draw(renderer, viewport, animProgress));

        _frame++;
        _renderTime += System.currentTimeMillis() - time;
    }

    public void init(GameConfig config, Game game) {
        _frame = 0;

        _renders.forEach(render -> {
            if (render.isActive(config)) {
                render.load();
            } else {
                render.unload();
            }
        });

        _renders.sort((r1, r2) -> r1.getLevel() - r2.getLevel());

        _worldRenderer = (WorldRenderer)getRender(WorldRenderer.class);
        _characterRenderer = (CharacterRenderer)getRender(CharacterRenderer.class);

        _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::init);
    }

    private BaseRenderer getRender(Class<? extends BaseRenderer> cls) {
        for (BaseRenderer renderer: _renders) {
            if (renderer.getClass() == cls) {
                return renderer;
            }
        }
        return null;
    }

    public static MainRenderer getInstance() {
        if (_self == null) {
            _self = new MainRenderer(GDXRenderer.getInstance(), Data.config);
        }
        return _self; }

    public static int getFrame() { return _frame; }

    public static long getRenderTime() { return _frame > 0 ? _renderTime / _frame : 0; }

    public List<BaseRenderer> getRenders() {
        return _renders;
    }

    public WorldRenderer getWorldRenderer() {
        return _worldRenderer;
    }

    public void toggleRender(BaseRenderer render) {
        if (render.isLoaded()) {
            render.unload();
        } else {
            render.load();
        }
    }
}
