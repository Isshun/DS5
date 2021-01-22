package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.character.CharacterInfoController;
import org.smallbox.faraway.client.font.FontManager;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.engine.RawColors;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterNeed.CharacterNeedModule;

import java.util.Comparator;

@GameObject
public class CrewController extends LuaController {
    private final static int GAUGE_WIDTH = 5;
    private final static int GAUGE_HEIGHT = 40;

    @Inject private CharacterModule characterModule;
    @Inject private CharacterNeedModule characterNeedModule;
    @Inject private MainPanelController mainPanelController;
    @Inject private CharacterInfoController characterInfoController;
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private FontManager fontManager;

    @BindLua private UIList listCrew;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        mainPanelController.addShortcut("Crew", this);
    }

    @Override
    public void onControllerUpdate() {
        if (listCrew != null) {
            Gdx.app.postRunnable(() -> {

                characterModule.getAll().forEach(character -> {

                    CompositeView view = (CompositeView)listCrew.createFromTemplate();

                    CharacterSkillExtra.SkillType skillType = character.getExtra(CharacterSkillExtra.class).getAll().stream().sorted(Comparator.comparingDouble(o -> o.level)).map(entry -> entry.type).findFirst().orElse(CharacterSkillExtra.SkillType.NONE);

                    UILabel lbCharacter = view.findLabel("lb_character_name");
                    UILabel lbSkill = view.findLabel("lb_character_skill");
                    lbCharacter.setText(StringUtils.upperCase(character.getName()));
                    lbSkill.setText(skillType.name());

                    switch (skillType) {
                        case HEAL: lbSkill.setTextColor(RawColors.RAW_BLUE); break;
                        case CRAFT: lbSkill.setTextColor(RawColors.RAW_RED); break;
                        case SEARCHER: lbSkill.setTextColor(RawColors.RAW_YELLOW); break;
                        case COOK: lbSkill.setTextColor(RawColors.RAW_GREEN); break;
                        case GATHER: lbSkill.setTextColor(RawColors.RAW_BLUE_LIGHT_3); break;
                        case DIG: lbSkill.setTextColor(RawColors.RAW_RED); break;
                        case BUILD: lbSkill.setTextColor(RawColors.RAW_YELLOW); break;
                        case STORE: lbSkill.setTextColor(RawColors.RAW_GREEN); break;
                        case CLEAN: lbSkill.setTextColor(RawColors.RAW_BLUE_LIGHT_3); break;
                    }

                    lbSkill.setTextColor(RawColors.RAW_BLUE_DARK_3);


                    GlyphLayout glyphLayout = new GlyphLayout();
                    glyphLayout.setText(fontManager.getFont(lbSkill.getFont(), lbSkill.getTextSize()), skillType.name());
//                    view.find("lb_character_job").setPosition((int) (glyphLayout.width + 20), 26);
                    lbSkill.setSize((int)glyphLayout.width + 3, 10);

                    view.findLabel("ic_skill").setText(skillType.name().substring(0, 1));

                    if (character.getJob() != null) {
                        view.findLabel("lb_character_job").setText(character.getJob().getLabel());
                    }

                    if (character.hasExtra(CharacterNeedsExtra.class)) {
                        CharacterNeedsExtra need = character.getExtra(CharacterNeedsExtra.class);
                        if (need != null) {
//                        view.find("gauge_food").setSize(GAUGE_WIDTH, (int) (GAUGE_HEIGHT * need.get(TAG_FOOD).value()));
//                        view.find("gauge_food").setPosition(1, (int) (GAUGE_HEIGHT - GAUGE_HEIGHT * need.get(TAG_FOOD).value()));
//                        view.find("gauge_health").setSize(GAUGE_WIDTH, (int) (GAUGE_HEIGHT * need.get(TAG_ENERGY).value()));
//                        view.find("gauge_health").setPosition(1, (int) (GAUGE_HEIGHT - GAUGE_HEIGHT * need.get(TAG_ENERGY).value()));
//                        view.find("gauge_social").setSize(GAUGE_WIDTH, (int) (GAUGE_HEIGHT * need.get(TAG_RELATION).value()));
//                        view.find("gauge_social").setPosition(1, (int) (GAUGE_HEIGHT - GAUGE_HEIGHT * need.get(TAG_RELATION).value()));
//                        view.find("gauge_entertainment").setSize(GAUGE_WIDTH, (int) (GAUGE_HEIGHT * need.get(TAG_ENTERTAINMENT).value()));
//                        view.find("gauge_entertainment").setPosition(1, (int) (GAUGE_HEIGHT - GAUGE_HEIGHT * need.get(TAG_ENTERTAINMENT).value()));
                        }
                    }

                    view.getEvents().setOnClickListener(() -> {
                        gameSelectionManager.select(character);
//                    ApplicationClient.notify(obs -> obs.onSelectCharacter(character));
//                    characterInfoController.display(character);
                    });

                    listCrew.addNextView(view);
                });
                listCrew.switchViews();

            });
        }
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_crew.lua");
//    }

}
