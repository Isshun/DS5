package org.smallbox.faraway.client.controller.character;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.RawColors;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;

@GameObject
public class CharacterInfoController extends AbsInfoLuaController<CharacterModel> {
    @Inject protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private CharacterModule characterModule;
    @Inject private CharacterInfoStatusController characterInfoStatusController;
    @Inject private CharacterInfoDetailsController characterInfoDetailsController;
    @Inject private CharacterInfoHealthController characterInfoHealthController;
    @Inject private CharacterInfoInventoryController characterInfoInventoryController;
    @Inject private CharacterInfoTimetableController characterInfoTimetableController;
    @Inject private CharacterInfoSkillsController characterInfoSkillsController;

    @BindLua private UILabel lbName;

    @BindLua private View btStatus;
    @BindLua private View btInventory;
    @BindLua private View btHealth;
    @BindLua private View btTimetable;
    @BindLua private View btInfo;

    private CharacterModel character;

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelection(this);
    }

    @Override
    public CharacterModel getObjectOnParcel(Parcel parcel) {
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

        openPage(characterInfoStatusController, btStatus);
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
        openPage(characterInfoStatusController, btStatus);
    }

//    @BindLuaAction
//    public void onOpenInfo(View view) {
//        openPage(characterInfoDetailsController, bgDetails);
//    }

    @BindLuaAction
    public void onOpenInventory(View view) {
        openPage(characterInfoInventoryController, btInventory);
    }

//    @BindLuaAction
//    public void onOpenSkill(View view) {
//        openPage(characterInfoSkillsController, bgSkills);
//    }

    @BindLuaAction
    public void onOpenHealth(View view) {
        openPage(characterInfoHealthController, btHealth);
    }

    @BindLuaAction
    public void onOpenTimetable(View view) {
        openPage(characterInfoTimetableController, btTimetable);
    }

    @BindLuaAction
    public void onOpenInfo(View view) {
        openPage(characterInfoTimetableController, btInfo);
    }

    private void openPage(LuaController controller, View button) {
        Arrays.asList(btStatus, btInventory, btHealth, btTimetable).forEach(view -> view.getStyle().setBackgroundColor(RawColors.RAW_YELLOW_50));
        button.getStyle().setBackgroundColor(RawColors.RAW_YELLOW);

        controller.setVisible(true);
    }

    @Override
    public void onControllerUpdate() {
        if (Objects.nonNull(character)) {

            if (Objects.nonNull(character.getParcel())) {
            }

        }
    }

    @OnGameSelectAction(CharacterModel.class)
    public void onSelectCharacter(CharacterModel character) {
        display(character);
    }

    @GameShortcut(key = Input.Keys.TAB)
    public void onNextTab() {
        if (characterInfoStatusController.isVisible()) {
            openPage(characterInfoInventoryController, btInventory);
//        } else if (characterInfoInventoryController.isVisible()) {
//            openPage(characterInfoDetailsController, bgDetails);
//        } else if (characterInfoDetailsController.isVisible()) {
//            openPage(characterInfoSkillsController, bgSkills);
        } else if (characterInfoSkillsController.isVisible()) {
            openPage(characterInfoHealthController, btHealth);
        } else if (characterInfoHealthController.isVisible()) {
            openPage(characterInfoTimetableController, btTimetable);
        } else if (characterInfoTimetableController.isVisible()) {
            openPage(characterInfoStatusController, btStatus);
        }
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_character.lua");
//        onDisplayUnique(characterModule.getAll().stream().findFirst().orElse(null));
//    }

}
