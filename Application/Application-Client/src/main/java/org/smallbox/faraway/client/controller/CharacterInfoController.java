package org.smallbox.faraway.client.controller;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterInfoController extends AbsInfoLuaController<CharacterModel> {

    @BindLuaController
    private CharacterStatusController   characterStatusController;

    @BindLuaController
    private CharacterHealthController   characterHealthController;

    @BindModule
    private CharacterModule characterModule;

    @BindLua private View               pageStatus;
    @BindLua private View               pageInventory;
    @BindLua private View               pageInfo;
    @BindLua private View               pageHealth;
    @BindLua private UILabel            lbName;
    @BindLua private UILabel            lbInfoBirth;
    @BindLua private UILabel            lbInfoEnlisted;
    @BindLua private UILabel            lbParcel;

    @BindLua private UILabel            lbTalents;

    @Override
    protected CharacterModel getObjectOnParcel(ParcelModel parcel) {
        return characterModule.getCharacter(parcel);
    }

    @Override
    protected void onDisplayUnique(CharacterModel character) {
        lbName.setText(character.getName());
        lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 47);
        lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 47);
        lbParcel.setText(character.getParcel().toString());

        // Info
        lbTalents.setText(StringUtils.join(character.getTalents().getAll().stream().map(talentEntry -> talentEntry.name).collect(Collectors.toList()), ", "));


        characterStatusController.selectCharacter(character);
        characterHealthController.selectCharacter(character);
    }

    @Override
    protected void onDisplayMultiple(List<CharacterModel> characterList) {
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
