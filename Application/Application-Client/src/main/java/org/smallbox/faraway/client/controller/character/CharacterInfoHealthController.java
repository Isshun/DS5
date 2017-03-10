package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.disease.DiseaseModule;

/**
 * Created by Alex on 11/12/2016.
 */
public class CharacterInfoHealthController extends LuaController {

    @BindModule
    private DiseaseModule diseaseModule;

    @BindLua
    private UIList listDiseases;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayDiseases(character);
    }

    private void displayDiseases(CharacterModel character) {
        listDiseases.clear();

        diseaseModule.getDiseases(character).forEach(disease ->
                listDiseases.addView(UILabel.create(null)
                        .setText(disease.info.label)
                        .setTextColor(new Color(0xB4D4D3))
                        .setTextSize(14)
                        .setSize(0, 20)));
    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}
