package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.character.CharacterInfoController;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterNeed.CharacterNeedModule;

import static org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra.TAG_FOOD;

@GameObject
public class CrewController extends LuaController {

    @BindLua
    private UIList listCrew;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private CharacterNeedModule characterNeedModule;

    @Inject
    private MainPanelController mainPanelController;

    @Inject
    private CharacterInfoController characterInfoController;

    @Inject
    private GameSelectionManager gameSelectionManager;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        mainPanelController.addShortcut("Crew", this);
    }

    @Override
    public void onControllerUpdate() {
        if (listCrew != null) {
            characterModule.getAll().forEach(character -> {

                View view = new UIFrame(null);
                view.setSize(200, 60);

                view.addView(UILabel.create(null)
                        .setText(character.getName())
                        .setTextSize(18)
                        .setTextColor(0xB4D4D3ff)
                        .setSize(300, 28)
                        .setPadding(8, 0));

                if (character.getJob() != null) {
                    view.addView(UILabel.create(null)
                            .setText(character.getJob().getLabel())
                            .setTextSize(14)
                            .setTextColor(0xB4D4D3ff)
                            .setSize(300, 28)
                            .setPosition(0, 22)
                            .setPadding(8, 0));
                }

                if (character.hasExtra(CharacterNeedsExtra.class)) {
                    CharacterNeedsExtra need = character.getExtra(CharacterNeedsExtra.class);
                    if (need != null) {
                        view.addView(createGaugeView("[base]/graphics/needs/ic_food.png", need.get(TAG_FOOD).value()).setPosition(270, 10));
                        view.addView(createGaugeView("[base]/graphics/needs/ic_health.png", 0.50).setPosition(270 + 20, 10));
                        view.addView(createGaugeView("[base]/graphics/needs/ic_social.png", 0.75).setPosition(270 + 40, 10));
                        view.addView(createGaugeView("[base]/graphics/needs/ic_entertainment.png", 0.75).setPosition(270 + 60, 10));
                    }
                }

                view.setOnClickListener((x, y) -> {
                    gameSelectionManager.select(character);
//                    ApplicationClient.notify(obs -> obs.onSelectCharacter(character));
//                    characterInfoController.display(character);
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
                .setBackgroundColor(0x0D4D4Bff));

        view.addView(new UIFrame(null)
                .setSize(10, (int) (30 * value))
                .setBackgroundColor(0x679B99ff)
                .setPosition(1, (int) (30 - 30 * value)));

        view.addView(UIImage.create(null)
                .setImage(iconPath)
                .setPosition(0, 34)
                .setSize(12, 12));

        return view;
    }

    @GameShortcut(key = Input.Keys.C)
    public void onPressT() {
        setVisible(true);
    }
}
