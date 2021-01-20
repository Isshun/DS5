package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.character.model.base.CharacterDiseasesExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.characterDisease.CharacterDiseaseModule;

@GameObject
public class CharacterInfoHealthController extends LuaController {
    @Inject private CharacterDiseaseModule diseaseModule;

    @BindLua private View imgHeart;
    @BindLua private View imgLeftArm;
    @BindLua private View imgLeftEye;
    @BindLua private View imgLeftHand;
    @BindLua private View imgLeftHead;
    @BindLua private View imgLeftLung;
    @BindLua private View imgLeftShoulder;
    @BindLua private View imgRightArm;
    @BindLua private View imgRightEye;
    @BindLua private View imgRightHand;
    @BindLua private View imgRightHead;
    @BindLua private View imgRightLung;
    @BindLua private View imgRightShoulder;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayDiseases(character);
    }

    private void displayDiseases(CharacterModel character) {
        if (character.hasExtra(CharacterDiseasesExtra.class)) {
            character.getExtra(CharacterDiseasesExtra.class).getAll().forEach(disease -> {
//                    listDiseases.addView(UILabel.create(null)
//                            .setText(disease.info.label)
//                            .setTextColor(ColorUtils.fromHex(0xB4D4D3ff))
//                            .setTextSize(14)
//                            .setSize(0, 20))
            });
        }
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_character_health.lua");
//        selectCharacter(_selected);
//    }

}
