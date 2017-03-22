package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterInfoSkillsController extends LuaController {

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

        _selected.getExtra(CharacterSkillExtra.class).getAll().forEach(skill -> {

            View view = new UIFrame(null)
                    .setBackgroundColor(skill.available ? 0x1a3647 : 0x0f1f29)
                    .setBorderColor(0x359f9f)
                    .setMargin(8, 0)
                    .setSize(320, 28);

            view.addView(UILabel.create(null)
                    .setText(skill.name)
                    .setTextColor(new Color(0x359f9f))
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
                public void onDrag(GameEvent event) {
                    Log.info("drag at " + event.mouseEvent.x + " x " + event.mouseEvent.y);
                    Log.info("drag on " + skill);
                }

                @Override
                public void onDrop(GameEvent event, View dropView) {
                    Log.info("drop at " + event.mouseEvent.x + " x " + event.mouseEvent.y);
                    Log.info("drop on " + dropView);
                    Log.info("drop on " + dropView.getData());

                    _selected.getExtra(CharacterSkillExtra.class).moveSkill(skill, ((CharacterSkillExtra.SkillEntry)dropView.getData()).index);
                    refreshSkills();
                }

                @Override
                public void onHover(GameEvent event, View dropView) {
                    dropView.setBackgroundColor(0xbb3647);
                }

                @Override
                public void onHoverExit(GameEvent event, View dropView) {
                    dropView.setBackgroundColor(0x1a3647);
                }
            });

            if (skill.available) {
                ApplicationClient.uiEventManager.addDropZone(view);
            }

            listSkills.addNextView(view);

        });

        listSkills.switchViews();
    }
}
