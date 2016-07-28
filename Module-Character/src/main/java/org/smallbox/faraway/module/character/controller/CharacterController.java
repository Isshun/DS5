package org.smallbox.faraway.module.character.controller;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.LuaPanelController;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.character.CharacterModuleObserver;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.Collection;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterController extends LuaPanelController {
    @BindLuaController
    private CharacterStatusController   statusController;

    @BindModule("")
    private CharacterModule             _characters;

    @BindModule("")
    private WorldInteractionModule      _worldInteraction;

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
        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
            @Override
            public void onSelect(Collection<ParcelModel> parcels) {
                CharacterModel selected = _characters.getCharacters().stream()
                        .filter(character -> parcels.contains(character.getParcel()))
                        .findAny().orElse(null);
                setVisible(selected != null);
                if (selected != null) {
                    select(selected);
                }
            }
        });
    }

    @Override
    public void gameStart(Game game) {
        _characters.addObserver(new CharacterModuleObserver() {
            @Override
            public void onSelectCharacter(CharacterModel character) {
                select(character);
            }
        });

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
        setVisible(true);

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
