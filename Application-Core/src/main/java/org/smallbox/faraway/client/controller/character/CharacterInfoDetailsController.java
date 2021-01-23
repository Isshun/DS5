package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.characterBuff.BuffType;
import org.smallbox.faraway.modules.characterBuff.CharacterBuffModule;

@GameObject
public class CharacterInfoDetailsController extends LuaController {

    @BindLua private UIList listDetails;
    @BindLua private UIList listBuffs;
    @Inject private CharacterBuffModule buffModule;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;

        displayBuffs(character);
    }

    private void displayBuffs(CharacterModel character) {
        buffModule.getBuffs(character)
                .stream()
                .filter(buffModel -> buffModel.getLevel() != null)
//                .sorted((o1, o2) -> o2.mood - o1.mood)
                .forEach(buff ->
                        listBuffs.addNextView(UILabel
                                .create(null)
//                                .setText("[" + (buff.mood > 0 ? "+" : "") + buff.mood  + "] " + buff.message)
                                .setText(buff.getLevel().getLabel())
                                .setTextSize(14)
                                .setTextColor(buff.getBuffType() == BuffType.BUFF ? 0x33bb88ff : 0xbb5555ff)
                                .setSize(300, 22)));

        listBuffs.switchViews();
    }

}
