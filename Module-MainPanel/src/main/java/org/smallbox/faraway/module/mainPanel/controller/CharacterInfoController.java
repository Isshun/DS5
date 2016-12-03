package org.smallbox.faraway.module.mainPanel.controller;

import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.mainPanel.MainPanelController;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterInfoController extends LuaController {

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

    private List<CharacterModel>        characterList;

    @Override
    public void onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE) {
            if (CollectionUtils.isNotEmpty(characterList)) {
                mainPanelController.setVisible(true);
                characterList = null;
            }
        }
    }

    @Override
    public boolean onClickOnParcel(List<ParcelModel> parcels) {
        characterList = parcels.stream()
                .map(parcel -> characterModule.getCharacter(parcel))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        refresh();

        return CollectionUtils.isNotEmpty(characterList);
    }

    private void refresh() {
        if (CollectionUtils.isNotEmpty(characterList)) {
            setVisible(true);

            if (characterList.size() == 1) {
                CharacterModel character = characterList.get(0);

                lbName.setText(character.getName());
                lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 47);
                lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 47);

                characterStatusController.selectCharacter(character);
            }
        } else {
            if (CollectionUtils.isNotEmpty(characterList)) {
                mainPanelController.setVisible(true);
                characterList = null;
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

    private void openPage(View page) {
        pageStatus.setVisible(false);
        pageInventory.setVisible(false);
        pageHealth.setVisible(false);
        pageInfo.setVisible(false);
        page.setVisible(true);
    }
}
