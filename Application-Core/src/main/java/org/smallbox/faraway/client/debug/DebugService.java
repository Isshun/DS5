package org.smallbox.faraway.client.debug;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.debug.dashboard.DashboardLayer;
import org.smallbox.faraway.client.debug.dashboard.MiniDashboardLayer;
import org.smallbox.faraway.client.debug.interpreter.DebugCommandInterpreterService;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationObject
public class DebugService {
    private final static String CONSOLE_ACCEPTABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<>@#$%^&_?!|:;\"',.()[]=/*-+ ";

    private boolean debugMode;
    private final StringBuilder commandInputSb = new StringBuilder();
    private final List<String> history = new ArrayList<>();
    private int historyIndex;

    @Inject private DashboardLayer dashboardLayer;
    @Inject private MiniDashboardLayer miniDashboardLayer;
    @Inject private DebugCommandInterpreterService debugCommandInterpreterService;

    @GameShortcut("debug/toggle")
    public void toggleDebugMode() {
        display(!debugMode);
    }

    private void display(boolean visible) {
        debugMode = visible;
        dashboardLayer.setVisibility(visible);
        miniDashboardLayer.setVisibility(!visible);
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void keyUp(int keycode) {
        switch (keycode) {

            case Input.Keys.ENTER:
                if (commandInputSb.toString().trim().length() > 0) {
                    debugCommandInterpreterService.execute(commandInputSb.toString());
                    history.add(commandInputSb.toString());
                    historyIndex = history.size();
                    commandInputSb.setLength(0);
                }
                break;

            case Input.Keys.BACKSPACE:
                commandInputSb.setLength(Math.max(commandInputSb.length() - 1, 0));
                break;

            case Input.Keys.UP:
                historyIndex = Math.max(historyIndex - 1, 0);
                commandInputSb.setLength(0);
                commandInputSb.append(history.get(historyIndex));
                break;

            case Input.Keys.DOWN:
                commandInputSb.setLength(0);
                historyIndex = Math.min(historyIndex + 1, history.size());
                if (historyIndex < history.size()) {
                    commandInputSb.append(history.get(historyIndex));
                }
                break;

            case Input.Keys.TAB:
                String fullCommand = debugCommandInterpreterService.autoComplete(commandInputSb.toString());
                if (fullCommand != null) {
                    commandInputSb.setLength(0);
                    commandInputSb.append(fullCommand);
                }
                break;

            case Input.Keys.PAGE_UP:
                dashboardLayer.pageUp();
                break;

            case Input.Keys.PAGE_DOWN:
                dashboardLayer.pageDown();
                break;

            case Input.Keys.F12:
                display(false);
                break;

        }

        debugCommandInterpreterService.setCommandInput(commandInputSb.toString());
    }

    public void typeCharacter(char character) {
        if (CONSOLE_ACCEPTABLE_CHARACTERS.indexOf(character) != -1) {
            commandInputSb.append(character);
        }

        debugCommandInterpreterService.setCommandInput(commandInputSb.toString());
    }

    public void click(int x, int y) {
        dashboardLayer.click(x, y);
    }

}
