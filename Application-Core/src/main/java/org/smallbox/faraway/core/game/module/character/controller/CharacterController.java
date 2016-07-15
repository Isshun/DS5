package org.smallbox.faraway.core.game.module.character.controller;

import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 25/04/2016.
 */
public class CharacterController extends LuaController {
//    @LegacyBindController("base.ui.info_character page_status")
    @BindLuaController private CharacterStatusController    statusController;

    @BindLua private View               pageStatus;
    @BindLua private View               pageInventory;
    @BindLua private View               pageInfo;
    @BindLua private View               pageHealth;
    @BindLua private UILabel            lbName;
    @BindLua private UILabel            lbInfoBirth;
    @BindLua private UILabel            lbInfoEnlisted;

    @Override
    protected void onCreate() {
        pageStatus.setOnClickListener(() -> {
            Log.debug("gg !");
        });
    }

    public void selectCharacter(CharacterModel character) {
        Log.debug("Select character: " + character);

        UserInterface.getInstance().findById("base.ui.panel_main").setVisible(false);

        pageStatus.setVisible(false);
        pageInventory.setVisible(false);
        pageHealth.setVisible(false);
        pageInfo.setVisible(false);

        lbName.setText(character.getName());
        lbInfoBirth.setDashedString("Birth", character.getPersonals().getEnlisted(), 47);
        lbInfoEnlisted.setDashedString("Enlisted", character.getPersonals().getEnlisted(), 47);

        statusController.selectCharacter(character);
    }

    @BindLuaAction
    public void onOpenStatus() {
        openPage(pageStatus);
    }

    @BindLuaAction
    public void onOpenInfo() {
        openPage(pageInfo);
    }

    @BindLuaAction
    public void onOpenInventory() {
        openPage(pageInventory);
    }

    @BindLuaAction
    public void onOpenHealth() {
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
