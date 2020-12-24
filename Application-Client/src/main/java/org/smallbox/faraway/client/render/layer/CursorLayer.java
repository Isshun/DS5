package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.client.GameActionManager;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.module.CharacterClientModule;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.CharacterPositionCommon;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

import java.util.Map;

@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class CursorLayer extends BaseLayer {

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private CharacterClientModule characterClientModule;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private GameManager gameManager;

    @Inject
    private InputManager inputManager;

    @Inject
    private GameActionManager gameActionManager;

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (gameActionManager.getMode() == GameActionManager.Mode.ADD_AREA) {
            renderer.drawText(inputManager.getMouseX() + 2, inputManager.getMouseY() + 2, 22, Color.BLACK, gameActionManager.getActionLabel());
            renderer.drawText(inputManager.getMouseX() + 1, inputManager.getMouseY() + 1, 22, Color.BLACK, gameActionManager.getActionLabel());
            renderer.drawText(inputManager.getMouseX(), inputManager.getMouseY(), 22, gameActionManager.getActionColor(), gameActionManager.getActionLabel());
        }
    }

}