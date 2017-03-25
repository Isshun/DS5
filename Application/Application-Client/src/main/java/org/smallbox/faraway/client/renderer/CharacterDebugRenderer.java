package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

@GameRenderer(level = MainRenderer.CHARACTER_RENDERER_LEVEL + 1, visible = false)
public class CharacterDebugRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule _characterModule;

    @BindComponent
    private SpriteManager _spriteManager;

    private int                     _floor;

    private static Color    COLOR_CRITICAL = new Color(0xbb0000);
    private static Color    COLOR_WARNING = new Color(0xbbbb00);
    private static Color    COLOR_OK = new Color(0x448800);

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _characterModule.getCharacters().forEach(character -> drawCharacter(renderer, viewport, character));
        _characterModule.getVisitors().forEach(visitor -> drawCharacter(renderer, viewport, visitor));
    }

    int k = 100; //increase k for more fidelity to the spline
    Vector2[] points = new Vector2[k];

    private void drawCharacter(GDXRenderer renderer, Viewport viewport, CharacterModel character) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();
        double viewPortScale = viewport.getScale();

//        if (character.getPath() != null) {
//            drawPath(character, viewport, renderer);
//            return;
//        }

        PathModel path = character.getPath();
        if (path != null && path._curve != null) {
//            double[] spline = path._spline;
//            double[] c = path._c;
//            for (int i = 0; i < c.length; i+=3) {
//                renderer.drawRectangle(100 + (int) (c[i] * 32), 100 + (int) (c[i + 1] * 32), 10, 10, Color.BLUE, true);
//            }
//            for (int i = 0; i < spline.length; i+=3) {
//                renderer.drawRectangle(100 + (int) (spline[i] * 32), 100 + (int) (spline[i + 1] * 32), 1, 1, Color.RED, true);
//            }
//            path._curve.forEach(vector3 -> renderer.drawRectangle(100 + (int) (vector3.x * 32), 100 + (int) (vector3.y * 32), 2, 2, Color.RED, true));

            points = new Vector2[2000];
            k = 0;
            for (float t = 0; t < 1; t += 0.001) {
                Vector2 out = new Vector2();
                points[k++] = out;
                path.myCatmull.valueAt(out, t);
                out.x = out.x * 32;
                out.y = out.y * 32;
//                path.myCatmull.derivativeAt(out, t);
                renderer.drawRectangle(viewPortX + (int) (out.x), viewPortY + (int) (out.y), 2, 2, Color.RED, true);
            }

            drawDebugPath(renderer, path, viewPortX, viewPortY);

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

            renderer.drawRectangle(posX, posY, 50, 12, Color.BLACK, true);
            renderer.drawText(posX + 2, posY + 2, 10, com.badlogic.gdx.graphics.Color.YELLOW, String.format("%.2f", character.getMoveProgress2()));
        }

    }

    private void drawDebugPath(GDXRenderer renderer, PathModel path, int viewPortX, int viewPortY) {
        path._nodes.forEach(node -> {
            int posX = viewPortX + (int) (node.x * 32);
            int posY = viewPortY + (int) (node.y * 32);
            renderer.drawRectangle(posX, posY, 4, 4, Color.BLUE, true);
            renderer.drawText(posX + 4, posY + 4, 12, com.badlogic.gdx.graphics.Color.BLUE, node.x + "x" + node.y);
        });
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

}