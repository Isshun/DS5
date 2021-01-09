package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.character.CharacterInfoController;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterNeed.CharacterNeedModule;

import static org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra.*;

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

                CompositeView view = (CompositeView)listCrew.createFromTemplate();

                view.findLabel("lb_character_name").setText(character.getName());

                if (character.getJob() != null) {
                    view.findLabel("lb_character_job").setText(character.getJob().getLabel());
                }

                if (character.hasExtra(CharacterNeedsExtra.class)) {
                    CharacterNeedsExtra need = character.getExtra(CharacterNeedsExtra.class);
                    if (need != null) {
                        view.findById("gauge_food").setSize(10, (int) (30 * need.get(TAG_FOOD).value()));
                        view.findById("gauge_food").setPosition(1, (int) (30 - 30 * need.get(TAG_FOOD).value()));
                        view.findById("gauge_health").setSize(10, (int) (30 * need.get(TAG_ENERGY).value()));
                        view.findById("gauge_health").setPosition(1, (int) (30 - 30 * need.get(TAG_ENERGY).value()));
                        view.findById("gauge_social").setSize(10, (int) (30 * need.get(TAG_RELATION).value()));
                        view.findById("gauge_social").setPosition(1, (int) (30 - 30 * need.get(TAG_RELATION).value()));
                        view.findById("gauge_entertainment").setSize(10, (int) (30 * need.get(TAG_ENTERTAINMENT).value()));
                        view.findById("gauge_entertainment").setPosition(1, (int) (30 - 30 * need.get(TAG_ENTERTAINMENT).value()));
                    }
                }

                view.getEvents().setOnClickListener((x, y) -> {
                    gameSelectionManager.select(character);
//                    ApplicationClient.notify(obs -> obs.onSelectCharacter(character));
//                    characterInfoController.display(character);
                });

                listCrew.addNextView(view);
            });
            listCrew.switchViews();
        }
    }

    @GameShortcut(key = Input.Keys.F1)
    public void onRefreshUI() {
        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_crew.lua");
    }

    @GameShortcut(key = Input.Keys.C)
    public void onPressT() {
        setVisible(true);
    }
}
