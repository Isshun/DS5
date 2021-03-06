package org.smallbox.faraway.client.controller.menu;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.render.MenuRender;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.client.ui.widgets.*;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerComplete;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnSettingsUpdate;

@ApplicationObject
public class MenuSettingsController extends LuaController {
    @Inject private MenuMainController menuMainController;
    @Inject private DependencyManager dependencyManager;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private ShortcutManager shortcutManager;
    @Inject private InputManager inputManager;
    @Inject private MenuRender menuRender;

    @BindLua private UILabel btGraphic;
    @BindLua private UILabel btSound;
    @BindLua private UILabel btBindings;
    @BindLua private UILabel btGameplay;
    @BindLua private UILabel btScreenBorderless;
    @BindLua private UILabel btScreenFullscreen;
    @BindLua private UILabel btScreenWindow;
    @BindLua private View graphicSubMenu;
    @BindLua private View soundSubMenu;
    @BindLua private UIList bindingsSubMenu;
    @BindLua private View gameplaySubMenu;
    @BindLua private View frameNewBinding;
    @BindLua private UISlider sliderMusic;

    private String screenMode;

    @OnApplicationLayerComplete
    public void onInit() {
        sliderMusic.setValue(applicationConfig.musicVolume);

        applicationConfig.shortcuts.forEach(configShortcut -> {
            CompositeView compositeView = bindingsSubMenu.createFromTemplate(CompositeView.class);
            compositeView.findLabel("lb_binding_name").setText(configShortcut.name);
            compositeView.findLabel("lb_binding_key").setText(configShortcut.key);
            compositeView.getEvents().setOnClickListener(() -> {
                compositeView.findLabel("lb_binding_key").setText("...");
                frameNewBinding.setVisible(true);
                menuRender.getNextKey((key, modifier) -> {
                    frameNewBinding.setVisible(false);
                    configShortcut.key = Input.Keys.toString(key);
                    compositeView.findLabel("lb_binding_key").setText(configShortcut.key);
                });
            });
            bindingsSubMenu.addNextView(compositeView);
        });

        bindingsSubMenu.switchViews();
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

        dependencyNotifier.notify(OnSettingsUpdate.class);
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
