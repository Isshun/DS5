package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.List;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterInfoController extends AbsInfoLuaController<CharacterModel> {

    @BindModule
    private CharacterModule characterModule;

    @BindLuaController
    private CharacterInfoStatusController characterInfoStatusController;

    @BindLuaController
    private CharacterInfoDetailsController characterInfoDetailsController;

    @BindLuaController
    private CharacterInfoHealthController characterInfoHealthController;

    @BindLuaController
    private CharacterInfoInventoryController characterInfoInventoryController;

    @BindLua private UILabel lbName;
    @BindLua private UILabel lbInfoBirth;
    @BindLua private UILabel lbInfoEnlisted;

    @BindLua private UIImage btStatus;
    @BindLua private UIImage btInventory;
    @BindLua private UIImage btDetails;
    @BindLua private UIImage btHealth;

    @BindLua private UIList  listTalents;

    @Override
    protected CharacterModel getObjectOnParcel(ParcelModel parcel) {
        return characterModule.getCharacter(parcel);
    }

    @Override
    protected void onDisplayUnique(CharacterModel character) {
        lbName.setText(character.getName());
        lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 43);
        lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 43);

        // Info
//        lbTalents.setText(StringUtils.join(character.getTalents().getAll().stream().map(talentEntry -> talentEntry.name).collect(Collectors.toList()), ", "));

        character.getTalents().getAll().forEach(talent ->
                listTalents.addView(UILabel.create(null).setText(talent.name).setTextColor(new Color(0xB4D4D3)).setTextSize(14).setSize(0, 20)));

        characterInfoStatusController.selectCharacter(character);
        characterInfoDetailsController.selectCharacter(character);
        characterInfoHealthController.selectCharacter(character);
        characterInfoInventoryController.selectCharacter(character);

        openPage(characterInfoStatusController, btStatus);
    }

    @Override
    protected void onDisplayMultiple(List<CharacterModel> characterList) {
    }

    @Override
    public void onGameStart(Game game) {
//        openPage(pageStatus, btOpenStatus);
    }

    @BindLuaAction
    public void onOpenStatus(View view) {
        openPage(characterInfoStatusController, btStatus);
    }

    @BindLuaAction
    public void onOpenInfo(View view) {
        openPage(characterInfoDetailsController, btDetails);
    }

    @BindLuaAction
    public void onOpenInventory(View view) {
        openPage(characterInfoInventoryController, btInventory);
    }

    @BindLuaAction
    public void onOpenHealth(View view) {
        openPage(characterInfoHealthController, btHealth);
    }

    private void openPage(LuaController controller, View image) {
        btStatus.setBackgroundColor(0x336688ff);
        btInventory.setBackgroundColor(0x336688ff);
        btHealth.setBackgroundColor(0x336688ff);
        btDetails.setBackgroundColor(0x336688ff);
        image.setBackgroundColor(0x5588bbff);
        controller.setVisible(true);
    }

    @Override
    public void onNewGameUpdate(Game game) {

    }

    @GameShortcut(key = GameEventListener.Key.TAB)
    public void onNextTab() {
        if (characterInfoStatusController.isVisible()) {
            openPage(characterInfoInventoryController, btInventory);
        } else if (characterInfoInventoryController.isVisible()) {
            openPage(characterInfoDetailsController, btDetails);
        } else if (characterInfoDetailsController.isVisible()) {
            openPage(characterInfoHealthController, btHealth);
        } else if (characterInfoHealthController.isVisible()) {
            openPage(characterInfoStatusController, btStatus);
        }
    }
}
