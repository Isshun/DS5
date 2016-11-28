package org.smallbox.faraway.module.character.controller;

import org.smallbox.faraway.core.game.GameEvent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.core.lua.BindLuaController;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.character.controller.LuaController;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.character.CharacterModuleObserver;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterController extends LuaController {
    @BindLuaController
    private CharacterStatusController   statusController;

    @BindModule
    private CharacterModule             _characters;

    @BindLua private View               pageStatus;
    @BindLua private View               pageInventory;
    @BindLua private View               pageInfo;
    @BindLua private View               pageHealth;
    @BindLua private UILabel            lbName;
    @BindLua private UILabel            lbInfoBirth;
    @BindLua private UILabel            lbInfoEnlisted;

    private CharacterModel              _selected;

    @Override
    protected void onGameCreate(Game game) {
        _characters.addObserver(new CharacterModuleObserver() {
            @Override
            public void onSelectCharacter(GameEvent event, CharacterModel character) {
                setVisible(true);
                select(character);
                event.consume();
            }
        });
    }

    @Override
    public void onGameStart(Game game) {
        openPage(pageStatus);
    }

    @BindLuaAction
    public void onOpenStatus(View view) {
        openPage(pageStatus);
    }

    @BindLuaAction
    public void onOpenInfo(View view) {
        openPage(pageInfo);
    }

    @BindLuaAction
    public void onOpenInventory(View view) {
        openPage(pageInventory);
    }

    @BindLuaAction
    public void onOpenHealth(View view) {
        openPage(pageHealth);
    }

    private void select(CharacterModel character) {
        Log.debug("Select character: " + character);

        _selected = character;
        lbName.setText(character.getName());
        lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 47);
        lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 47);

        statusController.selectCharacter(character);
    }

    private void openPage(View page) {
        pageStatus.setVisible(false);
        pageInventory.setVisible(false);
        pageHealth.setVisible(false);
        pageInfo.setVisible(false);
        page.setVisible(true);
    }

    public CharacterModel getSelected() {
        return _selected;
    }
}
