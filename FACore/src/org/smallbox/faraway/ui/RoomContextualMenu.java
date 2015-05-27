package org.smallbox.faraway.ui;

import java.util.List;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.room.Room;

public class RoomContextualMenu extends ContextualMenu {

	public RoomContextualMenu(int tileIndex, int x, int y, int width, int height, Viewport viewport, final Room room) {
		super(tileIndex, x, y, width, height, viewport);
		
		List<Character> characters = Game.getCharacterManager().getList();
		final ContextualMenu subMenu = new ContextualMenu(0, 100, 0, 160, (characters.size() + 1) * ContextualMenu.LINE_HEIGHT + ContextualMenu.PADDING_V * 2, viewport);
		subMenu.addEntry(Strings.LB_NOBODY, new OnClickListener() {
			@Override
			public void onClick(View view) {
				room.setOwner(null);
				setVisible(false);
			}
		}, null);
		for (final Character character: characters) {
			subMenu.addEntry(character.getName(), new OnClickListener() {
				@Override
				public void onClick(View view) {
					room.setOwner(character);
					setVisible(false);
				}
			}, null);
		}

		// Common room
		addEntry("common room:  " + (room.isCommon() ? "yes" : "no"), new OnClickListener() {
			@Override
			public void onClick(View view) {
				room.setCommon(!room.isCommon());
				((TextView)view).setString("common room:  " + (room.isCommon() ? "yes" : "no"));
			}
		}, null);

		// Owner
		addEntry("set owner", new OnClickListener() {
			@Override
			public void onClick(View view) {
				addSubMenu(1, subMenu);
			}
		}, null);

		setVisible(true);
	}

}
