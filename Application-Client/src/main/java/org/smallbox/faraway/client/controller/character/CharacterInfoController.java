package org.smallbox.faraway.client.controller.character;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Objects;
import java.util.Queue;

/**
 * Created by Alex on 25/04/2016.
 */
@GameObject
public class CharacterInfoController extends AbsInfoLuaController<CharacterModel> {

    @Inject
    protected SelectionManager selectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private CharacterInfoStatusController characterInfoStatusController;

    @Inject
    private CharacterInfoDetailsController characterInfoDetailsController;

    @Inject
    private CharacterInfoHealthController characterInfoHealthController;

    @Inject
    private CharacterInfoInventoryController characterInfoInventoryController;

    @Inject
    private CharacterInfoTimetableController characterInfoTimetableController;

    @Inject
    private CharacterInfoSkillsController characterInfoSkillsController;

    @BindLua private UILabel lbName;
    @BindLua private UILabel lbPosition;

    @BindLua private View bgStatus;
    @BindLua private View bgInventory;
    @BindLua private View bgDetails;
    @BindLua private View bgHealth;
    @BindLua private View bgSkills;
    @BindLua private View bgTimetable;

    private CharacterModel character;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelection(this);
    }

    @Override
    public CharacterModel getObjectOnParcel(ParcelModel parcel) {
        return characterModule.getCharacter(parcel);
    }

    @Override
    protected void onDisplayUnique(CharacterModel character) {
        this.character = character;

        lbName.setText(character.getName());

        // Info
//        lbSkills.setText(StringUtils.join(character.getSkills().getAll().stream().map(skillEntry -> skillEntry.name).collect(Collectors.toList()), ", "));

        characterInfoStatusController.selectCharacter(character);
        characterInfoDetailsController.selectCharacter(character);
        characterInfoHealthController.selectCharacter(character);
        characterInfoInventoryController.selectCharacter(character);
        characterInfoTimetableController.selectCharacter(character);
        characterInfoSkillsController.selectCharacter(character);

        openPage(characterInfoStatusController, bgStatus);
    }

    @Override
    protected void onDisplayMultiple(Queue<CharacterModel> characterList) {
    }

    @Override
    public void onGameStart(Game game) {
//        openPage(pageStatus, btOpenStatus);
    }

    @BindLuaAction
    public void onOpenStatus(View view) {
        openPage(characterInfoStatusController, bgStatus);
    }

    @BindLuaAction
    public void onOpenInfo(View view) {
        openPage(characterInfoDetailsController, bgDetails);
    }

    @BindLuaAction
    public void onOpenInventory(View view) {
        openPage(characterInfoInventoryController, bgInventory);
    }

    @BindLuaAction
    public void onOpenSkill(View view) {
        openPage(characterInfoSkillsController, bgSkills);
    }

    @BindLuaAction
    public void onOpenHealth(View view) {
        openPage(characterInfoHealthController, bgHealth);
    }

    @BindLuaAction
    public void onOpenTimetable(View view) {
        openPage(characterInfoTimetableController, bgTimetable);
    }

    private void openPage(LuaController controller, View bg) {
        bgStatus.setBackgroundColor(0x132733ff);
        bgInventory.setBackgroundColor(0x132733ff);
        bgHealth.setBackgroundColor(0x132733ff);
        bgDetails.setBackgroundColor(0x132733ff);
        bgTimetable.setBackgroundColor(0x132733ff);
        bgSkills.setBackgroundColor(0x132733ff);
        controller.setVisible(true);
        bg.setBackgroundColor(0x359f9fff);
    }

    @Override
    public void onControllerUpdate() {
        if (Objects.nonNull(character)) {

            if (Objects.nonNull(character.getParcel())) {
                lbPosition.setText(character.getParcel().x + "x" + character.getParcel().y + "x" + character.getParcel().z);
            }

        }
    }

    @GameShortcut(key = Input.Keys.TAB)
    public void onNextTab() {
        if (characterInfoStatusController.isVisible()) {
            openPage(characterInfoInventoryController, bgInventory);
        } else if (characterInfoInventoryController.isVisible()) {
            openPage(characterInfoDetailsController, bgDetails);
        } else if (characterInfoDetailsController.isVisible()) {
            openPage(characterInfoSkillsController, bgSkills);
        } else if (characterInfoSkillsController.isVisible()) {
            openPage(characterInfoHealthController, bgHealth);
        } else if (characterInfoHealthController.isVisible()) {
            openPage(characterInfoTimetableController, bgTimetable);
        } else if (characterInfoTimetableController.isVisible()) {
            openPage(characterInfoStatusController, bgStatus);
        }
    }
}
