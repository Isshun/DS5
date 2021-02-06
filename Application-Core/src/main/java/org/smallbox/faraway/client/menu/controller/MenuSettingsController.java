package org.smallbox.faraway.client.menu.controller;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UISlider;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnSettingsUpdate;

@ApplicationObject
public class MenuSettingsController extends LuaController {
    @Inject private MenuMainController menuMainController;
    @Inject private DependencyManager dependencyManager;
    @Inject private ApplicationConfig applicationConfig;

    @BindLua private UILabel btGraphic;
    @BindLua private UILabel btSound;
    @BindLua private UILabel btBindings;
    @BindLua private UILabel btGameplay;
    @BindLua private UILabel btScreenBorderless;
    @BindLua private UILabel btScreenFullscreen;
    @BindLua private UILabel btScreenWindow;
    @BindLua private View graphicSubMenu;
    @BindLua private View soundSubMenu;
    @BindLua private View bindingsSubMenu;
    @BindLua private View gameplaySubMenu;
    @BindLua private UISlider sliderMusic;

    private String screenMode;

    @AfterApplicationLayerInit
    public void onInit() {
        sliderMusic.setValue(applicationConfig.musicVolume);
    }

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
        applicationConfig.musicVolume = sliderMusic.getValue();
        applicationConfig.screen.mode = screenMode;

        dependencyManager.callMethodAnnotatedBy(OnSettingsUpdate.class);
    }

    @BindLuaAction
    public void setScreenBorderless(View view) {
        btScreenBorderless.setText("[x] Borderless");
        btScreenFullscreen.setText("[ ] Fullscreen");
        btScreenWindow.setText("[ ] Window");
        screenMode = "borderless";
    }

    @BindLuaAction
    public void setScreenFullscreen(View view) {
        btScreenBorderless.setText("[ ] Borderless");
        btScreenFullscreen.setText("[x] Fullscreen");
        btScreenWindow.setText("[ ] Window");
        screenMode = "fullscreen";
    }

    @BindLuaAction
    public void setScreenWindow(View view) {
        btScreenBorderless.setText("[ ] Borderless");
        btScreenFullscreen.setText("[ ] Fullscreen");
        btScreenWindow.setText("[x] Window");
        screenMode = "window";
    }

    private void openSubMenu(UILabel button, View subMenu) {
        btGraphic.setTextColor(0x00000088);
        btSound.setTextColor(0x00000088);
        btBindings.setTextColor(0x00000088);
        btGameplay.setTextColor(0x00000088);

        button.setTextColor(0xffffffcc);

        graphicSubMenu.setVisible(false);
        soundSubMenu.setVisible(false);
        bindingsSubMenu.setVisible(false);
        gameplaySubMenu.setVisible(false);

        subMenu.setVisible(true);
    }

}
