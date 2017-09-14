package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.dependencyInjector.GameObject;

/**
 * Created by Alex on 11/12/2016.
 */
@GameObject
public class CharacterInfoHealthController extends LuaController {

//    @BindComponent
//    private CharacterDiseaseModule diseaseModule;

    @BindLua
    private UIList listDiseases;

    private CharacterCommon _selected;

    public void selectCharacter(CharacterCommon character) {
        _selected = character;

//        displayDiseases(character);
    }

//    private void displayDiseases(CharacterModel character) {
//        listDiseases.removeAllViews();
//
//        if (character.hasExtra(CharacterDiseasesExtra.class)) {
//            character.getExtra(CharacterDiseasesExtra.class).getAll().forEach(disease ->
//                    listDiseases.addView(UILabel.create(null)
//                            .setText(disease.info.label)
//                            .setTextColor(ColorUtils.fromHex(0xB4D4D3ff))
//                            .setTextSize(14)
//                            .setSize(0, 20)));
//        }
//    }

}
