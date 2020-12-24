package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

@GameObject
public class CharacterInfoSkillsController extends LuaController {

    @Inject
    private UIEventManager uiEventManager;

    @BindLua
    private UIList listSkills;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;
        listSkills.removeAllViews();
    }

    @Override
    protected void onControllerUpdate() {

        if (_selected != null && listSkills.getViews().isEmpty()) {
            refreshSkills();
        }

    }

    private void refreshSkills() {

        if (_selected.hasExtra(CharacterSkillExtra.class)) {
            _selected.getExtra(CharacterSkillExtra.class).getAll().forEach(skill -> {

                View view = new UIFrame(null)
                        .setBackgroundColor(skill.available ? 0x1a3647ff : 0x0f1f29ff)
                        .setBorderColor(0x359f9fff)
                        .setMargin(8, 0)
                        .setSize(320, 28);

                view.addView(UILabel.create(null)
                        .setText(skill.name)
                        .setTextColor(ColorUtils.fromHex(0x359f9fff))
                        .setTextSize(16)
                        .setPosition(8, 16)
                        .setSize(320, 28));

                int width = Utils.round(skill.level * 10, 10);
                view.addView(UIImage.create(null)
                        .setImage("[base]/graphics/needbar.png")
                        .setTextureRect(0, 0, width, 8)
                        .setPosition(314 - width, 18));

                view.setData(skill);

                view.setOnDragListener(new UIEventManager.OnDragListener() {
                    @Override
                    public void onDrag(int x, int y) {
                        Log.info("drag at " + x + " x " + y);
                        Log.info("drag on " + skill);
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
                        dropView.setBackgroundColor(0xbb3647ff);
                    }

                    @Override
                    public void onHoverExit(int x, int y, View dropView) {
                        dropView.setBackgroundColor(0x1a3647ff);
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
