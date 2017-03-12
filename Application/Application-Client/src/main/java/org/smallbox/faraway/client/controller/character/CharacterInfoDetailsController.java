package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterInfoDetailsController extends LuaController {

    @BindLua
    private UIList listTalents;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;
        listTalents.removeAllViews();
    }

    @Override
    protected void onNewGameUpdate(Game game) {

        if (_selected != null && listTalents.getViews().isEmpty()) {
            refreshTalents();
        }

    }

    private void refreshTalents() {

        _selected.getTalents().getAll().forEach(talent -> {

            View view = new UIFrame(null)
                    .setBackgroundColor(talent.available ? 0x1a3647 : 0x0f1f29)
                    .setBorderColor(0x359f9f)
                    .setMargin(8, 0)
                    .setSize(320, 28);

            view.addView(UILabel.create(null)
                    .setText(talent.name)
                    .setTextColor(new Color(0x359f9f))
                    .setTextSize(16)
                    .setPosition(8, 16)
                    .setSize(320, 28));

            int width = Utils.round(talent.level * 10, 10);
            view.addView(UIImage.create(null)
                    .setImage("[base]/graphics/needbar.png")
                    .setTextureRect(0, 0, width, 8)
                    .setPosition(314 - width, 18));

            view.setData(talent);

            view.setOnDragListener(new UIEventManager.OnDragListener() {
                @Override
                public void onDrag(GameEvent event) {
                    Log.info("drag at " + event.mouseEvent.x + " x " + event.mouseEvent.y);
                    Log.info("drag on " + talent);
                }

                @Override
                public void onDrop(GameEvent event, View dropView) {
                    Log.info("drop at " + event.mouseEvent.x + " x " + event.mouseEvent.y);
                    Log.info("drop on " + dropView);
                    Log.info("drop on " + dropView.getData());

                    _selected.getTalents().moveTalent(talent, ((CharacterTalentExtra.TalentEntry)dropView.getData()).index);
                    refreshTalents();
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

            if (talent.available) {
                ApplicationClient.uiEventManager.addDropZone(view);
            }

            listTalents.addNextView(view);

        });

        listTalents.switchViews();
    }
}
