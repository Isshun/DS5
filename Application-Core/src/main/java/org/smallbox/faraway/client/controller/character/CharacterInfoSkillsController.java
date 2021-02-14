package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.log.Log;

@GameObject
public class CharacterInfoSkillsController extends LuaController {
    @Inject private UIEventManager uiEventManager;

    @BindLua
    private UIList listSkills;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;
        listSkills.removeAllViews();
    }

    @OnGameUpdate(runOnMainThread = true)
    protected void onControllerUpdate() {
        if (_selected != null && listSkills.getViews().isEmpty()) {
            refreshSkills();
        }
    }

    private void refreshSkills() {

        if (_selected.hasExtra(CharacterSkillExtra.class)) {
            _selected.getExtra(CharacterSkillExtra.class).getAll().forEach(skill -> {
                int width = Utils.round(skill.level * 10, 10);
                UIFrame view = listSkills.createFromTemplate(UIFrame.class);

                view.getStyle().setBackgroundColor(skill.available ? 0x1a3647ff : 0x0f1f29ff);
                view.findLabel("lb_skill").setText(skill.name);
                view.findImage("gauge_skill").setTextureRect(0, 0, width, 8);
                view.findImage("gauge_skill").setPosition(314 - width, 18);
                view.setData(skill);

                view.getEvents().setOnDragListener(new UIEventManager.OnDragListener() {
                    @Override
                    public void onDrag(int x, int y) {
                        Log.info("drag at " + x + " x " + y);
                        Log.info("drag on " + skill);
                    }

                    @Override
                    public void onDragMove(int x, int y) {
                    }

                    @Override
                    public void onDrop(int x, int y, View dropView) {
                        Log.info("drop at " + x + " x " + y);
                        Log.info("drop on " + dropView);
                        Log.info("drop on " + dropView.getData());

                        _selected.getExtra(CharacterSkillExtra.class).moveSkill(skill, ((CharacterSkillExtra.SkillEntry) dropView.getData()).index);
                        refreshSkills();
                    }

                    @Override
                    public void onHover(int x, int y, View dropView) {
                        dropView.getStyle().setBackgroundColor(0xbb3647ff);
                    }

                    @Override
                    public void onHoverExit(int x, int y, View dropView) {
                        dropView.getStyle().setBackgroundColor(0x1a3647ff);
                    }
                });

                if (skill.available) {
                    uiEventManager.addDropZone(view);
                }

                listSkills.addNextView(view);
            });
        }

        listSkills.switchViews();
    }
}
