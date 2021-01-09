package org.smallbox.faraway.client.menu.controller;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class MenuSettingsController extends LuaController {

    @Inject
    private MenuMainController menuMainController;

    @BindLua private View btGraphic;
    @BindLua private View btSound;
    @BindLua private View btBindings;
    @BindLua private View btGameplay;
    @BindLua private UILabel btScreenBorderless;
    @BindLua private UILabel btScreenFullscreen;
    @BindLua private UILabel btScreenWindow;
    @BindLua private View graphicSubMenu;
    @BindLua private View soundSubMenu;
    @BindLua private View bindingsSubMenu;
    @BindLua private View gameplaySubMenu;

    @BindLuaAction
    public void onOpenGraphic(View view) {
        openSubMenu(btGraphic, graphicSubMenu);
    }

    @BindLuaAction
    public void onOpenSound(View view) {
        openSubMenu(btSound, soundSubMenu);
    }

    @BindLuaAction
    public void onOpenBindings(View view) {
        openSubMenu(btBindings, bindingsSubMenu);
    }

    @BindLuaAction
    public void onOpenGameplay(View view) {
        openSubMenu(btGameplay, gameplaySubMenu);
    }

    @BindLuaAction
    public void onClose(View view) {
        setVisible(false);
        menuMainController.setVisible(true);
    }

    @BindLuaAction
    public void onApply(View view) {
    }

    @BindLuaAction
    public void setScreenBorderless(View view) {
        btScreenBorderless.setText("[x] Borderless");
        btScreenFullscreen.setText("[ ] Fullscreen");
        btScreenWindow.setText("[ ] Window");
    }

    @BindLuaAction
    public void setScreenFullscreen(View view) {
        btScreenBorderless.setText("[ ] Borderless");
        btScreenFullscreen.setText("[x] Fullscreen");
        btScreenWindow.setText("[ ] Window");
    }

    @BindLuaAction
    public void setScreenWindow(View view) {
        btScreenBorderless.setText("[ ] Borderless");
        btScreenFullscreen.setText("[ ] Fullscreen");
        btScreenWindow.setText("[x] Window");
    }

    private void openSubMenu(View button, View subMenu) {
        btGraphic.getStyle().setBackgroundColor(Colors.BLUE_DARK_4);
        btSound.getStyle().setBackgroundColor(Colors.BLUE_DARK_4);
        btBindings.getStyle().setBackgroundColor(Colors.BLUE_DARK_4);
        btGameplay.getStyle().setBackgroundColor(Colors.BLUE_DARK_4);

        button.getStyle().setBackgroundColor(0x25c9cbff);

        graphicSubMenu.setVisible(false);
        soundSubMenu.setVisible(false);
        bindingsSubMenu.setVisible(false);
        gameplaySubMenu.setVisible(false);

        subMenu.setVisible(true);
    }

}
