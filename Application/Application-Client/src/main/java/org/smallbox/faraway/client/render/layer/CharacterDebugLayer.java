package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

@GameLayer(level = LayerManager.CHARACTER_LAYER_LEVEL + 1, visible = false)
public class CharacterDebugLayer extends BaseLayer {

    @BindModule
    private CharacterModule _characterModule;

    @BindComponent
    private SpriteManager   _spriteManager;

    private int                     _floor;

    private static Color COLOR_CRITICAL = ColorUtils.fromHex(0xbb0000);
    private static Color COLOR_WARNING = ColorUtils.fromHex(0xbbbb00);
    private static Color COLOR_OK = ColorUtils.fromHex(0x448800);

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _characterModule.getCharacters().forEach(character -> drawCharacter(renderer, viewport, character));
        _characterModule.getVisitors().forEach(visitor -> drawCharacter(renderer, viewport, visitor));
    }

    private void drawCharacter(GDXRenderer renderer, Viewport viewport, CharacterModel character) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();

        PathModel path = character.getPath();
        if (path != null && path._curve != null) {
            drawDebugPathPath(renderer, path, viewPortX, viewPortY);
            drawDebugPathStep(renderer, path, viewPortX, viewPortY);
            drawDebugPosition(renderer, path, viewPortX, viewPortY, character);
        }
    }

    private void drawDebugPathPath(GDXRenderer renderer, PathModel path, int viewPortX, int viewPortY) {
        for (float t = 0; t < 1; t += 0.001) {
            Vector2 out = new Vector2();
            path.myCatmull.valueAt(out, t);
            renderer.drawRectangle(viewPortX + (int) (out.x * 32), viewPortY + (int) (out.y * 32), 2, 2, ColorUtils.RED, true);
        }
    }

    private void drawDebugPosition(GDXRenderer renderer, PathModel path, int viewPortX, int viewPortY, CharacterModel character) {
        //            current += Gdx.graphics.getDeltaTime() * speed;
//            if(current >= 1)
//                current -= 1;
//            float place = current * k;
//            Vector2 first = points[(int)place];
//            Vector2 second;
//            if(((int)place+1) < k) {
//                second = points[(int)place+1];
//            } else {
//                second = points[0]; //or finish, in case it does not loop.
//            }
//            float t = place - ((int)place); //the decimal part of place
        double t = character.getMoveProgress2() / path.getLength();
        Vector2 out = new Vector2();
        path.myCatmull.valueAt(out, (float) t);
        out.x = out.x * 32;
        out.y = out.y * 32;

        int posX = (int) (viewPortX + out.x);
        int posY = (int) (viewPortY + out.y);
//            int posX = viewPortX + (int)(first.x + (second.x - first.x) * t);
//            int posY = viewPortY + (int)(first.y + (second.y - first.y) * t);

        renderer.drawRectangle(posX, posY, 50, 12, ColorUtils.BLACK, true);
        renderer.drawText(posX + 2, posY + 2, 10, com.badlogic.gdx.graphics.Color.YELLOW, String.format("%.2f", character.getMoveProgress2()));
    }

    private void drawDebugPathStep(GDXRenderer renderer, PathModel path, int viewPortX, int viewPortY) {
        path._nodes.forEach(node -> {
            int posX = viewPortX + node.x * 32;
            int posY = viewPortY + node.y * 32;
            renderer.drawRectangle(posX, posY, 4, 4, ColorUtils.BLUE, true);
            renderer.drawText(posX + 4, posY + 4, 12, com.badlogic.gdx.graphics.Color.BLUE, node.x + "x" + node.y);
        });
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

}