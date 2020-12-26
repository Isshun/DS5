package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.modules.character.model.base.CharacterDiseasesExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.characterDisease.CharacterDiseaseModule;

@GameObject
public class CharacterInfoHealthController extends LuaController {

    @Inject
    private CharacterDiseaseModule diseaseModule;

    @BindLua
    private UIList listDiseases;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayDiseases(character);
    }

    private void displayDiseases(CharacterModel character) {
        listDiseases.removeAllViews();

        if (character.hasExtra(CharacterDiseasesExtra.class)) {
            character.getExtra(CharacterDiseasesExtra.class).getAll().forEach(disease ->
                    listDiseases.addView(UILabel.create(null)
                            .setText(disease.info.label)
                            .setTextColor(ColorUtils.fromHex(0xB4D4D3ff))
                            .setTextSize(14)
                            .setSize(0, 20)));
        }
    }

}
