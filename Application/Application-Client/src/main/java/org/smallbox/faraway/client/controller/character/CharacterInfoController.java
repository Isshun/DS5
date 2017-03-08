package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.List;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterInfoController extends AbsInfoLuaController<CharacterModel> {

    @BindLuaController
    private CharacterStatusController   characterStatusController;

    @BindLuaController
    private CharacterHealthController   characterHealthController;

    @BindLuaController
    private CharacterInventoryController   characterInventoryController;

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

    @BindLua private UIList             listTalents;

    @Override
    protected CharacterModel getObjectOnParcel(ParcelModel parcel) {
        return characterModule.getCharacter(parcel);
    }

    @Override
    protected void onDisplayUnique(CharacterModel character) {
        lbName.setText(character.getName());
        lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 43);
        lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 43);
        lbParcel.setText(character.getParcel().toString());

        // Info
//        lbTalents.setText(StringUtils.join(character.getTalents().getAll().stream().map(talentEntry -> talentEntry.name).collect(Collectors.toList()), ", "));

        character.getTalents().getAll().forEach(talent ->
                listTalents.addView(UILabel.create(null).setText(talent.name).setTextColor(new Color(0xB4D4D3)).setTextSize(14).setSize(0, 20)));

        characterStatusController.selectCharacter(character);
        characterHealthController.selectCharacter(character);
        characterInventoryController.selectCharacter(character);

        openPage(pageStatus);
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

    @Override
    public void onNewGameUpdate(Game game) {

    }
}
