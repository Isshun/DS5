package org.smallbox.faraway.client.manager.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class WorldInputManager implements InputProcessor {
    private final boolean[] keyDirections = new boolean[4];

    public boolean[] getDirection() {
        return keyDirections;
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            keyDirections[0] = true;
            return true;
        }

        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            keyDirections[1] = true;
            return true;
        }

        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            keyDirections[2] = true;
            return true;
        }

        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            keyDirections[3] = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            keyDirections[0] = false;
            return true;
        }

        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            keyDirections[1] = false;
            return true;
        }

        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            keyDirections[2] = false;
            return true;
        }

        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            keyDirections[3] = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
