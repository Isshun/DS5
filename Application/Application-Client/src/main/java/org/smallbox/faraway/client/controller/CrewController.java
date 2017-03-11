package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.controller.character.CharacterInfoController;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterNeedModule;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;

import static org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra.TAG_FOOD;

/**
 * Created by Alex on 25/07/2016.
 */
public class CrewController extends LuaController {

    @BindLua
    private UIList listCrew;

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private CharacterNeedModule characterNeedModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindLuaController
    private CharacterInfoController characterInfoController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Crew", this);
    }

    @Override
    public void onNewGameUpdate(Game game) {
        if (listCrew != null) {
            characterModule.getCharacters().forEach(character -> {

                View view = new UIFrame(null);
                view.setSize(200, 60);

                view.addView(UILabel.create(null)
                        .setText(character.getName())
                        .setTextSize(18)
                        .setTextColor(0xB4D4D3)
                        .setSize(300, 28)
                        .setPadding(8, 0));

                if (character.getJob() != null) {
                    view.addView(UILabel.create(null)
                            .setText(character.getJob().getLabel())
                            .setTextSize(14)
                            .setTextColor(0xB4D4D3)
                            .setSize(300, 28)
                            .setPosition(0, 22)
                            .setPadding(8, 0));
                }

                CharacterNeedsExtra need = character.getExtra(CharacterNeedsExtra.class);
                if (need != null) {
                    view.addView(createGaugeView("[base]/graphics/needs/ic_food.png", need.get(TAG_FOOD).value()).setPosition(270, 10));
                    view.addView(createGaugeView("[base]/graphics/needs/ic_health.png", 0.50).setPosition(270 + 20, 10));
                    view.addView(createGaugeView("[base]/graphics/needs/ic_social.png", 0.75).setPosition(270 + 40, 10));
                    view.addView(createGaugeView("[base]/graphics/needs/ic_entertainment.png", 0.75).setPosition(270 + 60, 10));
                }

                view.setOnClickListener((GameEvent event) -> {
                    ApplicationClient.notify(obs -> obs.onSelectCharacter(character));
                    characterInfoController.display(character);
                });

                listCrew.addNextView(view);
            });
            listCrew.switchViews();
        }
    }

    private View createGaugeView(String iconPath, double value) {
        View view = new UIFrame(null);

        view.addView(new UIFrame(null)
                .setSize(10, 30)
                .setPosition(1, 0)
                .setBackgroundColor(0x0D4D4B));

        view.addView(new UIFrame(null)
                .setSize(10, (int) (30 * value))
                .setBackgroundColor(0x679B99)
                .setPosition(1, (int) (30 - 30 * value)));

        view.addView(UIImage.create(null)
                .setImage(iconPath)
                .setPosition(0, 34)
                .setSize(12, 12));

        return view;
    }

    @GameShortcut(key = GameEventListener.Key.BACKSPACE)
    public void onEscape() {
        if (isVisible()) {
            mainPanelController.setVisible(true);
        }
    }

}
