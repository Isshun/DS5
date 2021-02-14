package org.smallbox.faraway.client.input;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class CameraMoveInputManager {
    private boolean movingLeft;
    private boolean movingRight;
    private boolean movingUp;
    private boolean movingDown;

    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            movingLeft = true;
            return true;
        }

        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            movingUp = true;
            return true;
        }

        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            movingRight = true;
            return true;
        }

        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            movingDown = true;
            return true;
        }

        return false;
    }

    public boolean keyUp(int keycode) {

        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            movingLeft = false;
            return true;
        }

        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            movingUp = false;
            return true;
        }

        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            movingRight = false;
            return true;
        }

        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            movingDown = false;
            return true;
        }

        return false;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public boolean isMovingUp() {
        return movingUp;
    }

    public boolean isMovingDown() {
        return movingDown;
    }

}
