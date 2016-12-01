package org.smallbox.faraway.module.mainPanel.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.character.CharacterModuleObserver;
import org.smallbox.faraway.module.mainPanel.MainPanelController;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterController extends LuaController {

    @BindLuaController
    private CharacterStatusController   characterStatusController;

    @BindModule
    private CharacterModule             characterModule;

    @BindLuaController
    private MainPanelController         mainPanelController;

    @BindLua private View               pageStatus;
    @BindLua private View               pageInventory;
    @BindLua private View               pageInfo;
    @BindLua private View               pageHealth;
    @BindLua private UILabel            lbName;
    @BindLua private UILabel            lbInfoBirth;
    @BindLua private UILabel            lbInfoEnlisted;

    private CharacterModel              _selected;

    @Override
    public void onReloadUI() {
        characterModule.addObserver(new CharacterModuleObserver() {
            @Override
            public void onSelectCharacter(GameEvent event, CharacterModel character) {
                setVisible(true);
                select(character);
                event.consume();
            }
        });
    }

    @Override
    public void onClickOnParcel(ParcelModel parcel) {
        if (parcel != null){
            CharacterModel character = characterModule.getCharacterAtPos(parcel.x, parcel.y, parcel.z);
            if (character != null) {
                select(character);
            }
        }
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
        setVisible(true);
        lbName.setText(character.getName());
        lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 47);
        lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 47);

        characterStatusController.selectCharacter(character);
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

//    @Override
//    public boolean onKey(GameEvent event, GameEventListener.Key key) {
//        if (key == GameEventListener.Key.TAB) {
//            select(event, getNext(_controller.getSelected()));
//            return true;
//        }
//        return false;
//    }
//
//    private CharacterModel getNext(CharacterModel currentCharacter) {
//        if (CollectionUtils.isNotEmpty(_characters)) {
//            Iterator<CharacterModel> iterator = _characters.iterator();
//            while (iterator.hasNext()) {
//                if (iterator.next() == currentCharacter) {
//                    break;
//                }
//            }
//            if (iterator.hasNext()) {
//                return iterator.next();
//            } else {
//                return _characters.iterator().next();
//            }
//        }
//        return null;
//    }

}
