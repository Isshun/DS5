//package org.smallbox.faraway.ui;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.Strings;
//import org.smallbox.faraway.ui.engine.OnClickListener;
//import org.smallbox.faraway.ui.engine.TextView;
//import org.smallbox.faraway.ui.engine.View;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.room.Room;
//
//import java.util.List;
//
//public class RoomContextualMenu extends ContextualMenu {
//
//	public RoomContextualMenu(int tileIndex, int x, int y, int width, int height, Viewport viewport, final Room room) {
//		super(tileIndex, x, y, width, height, viewport);
//
//		List<CharacterModel> list = Game.getCharacterManager().getCharacters();
//		final ContextualMenu subMenu = new ContextualMenu(0, 100, 0, 160, (list.size() + 1) * ContextualMenu.LINE_HEIGHT + ContextualMenu.PADDING_V * 2, viewport);
//		subMenu.addEntry(Strings.LB_NOBODY, new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				room.setOwner(null);
//				setVisible(false);
//			}
//		}, null);
//		for (final CharacterModel character: list) {
//			subMenu.addEntry(character.getName(), new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					room.setOwner(character);
//					setVisible(false);
//				}
//			}, null);
//		}
//
//		// Common room
//		addEntry("common room:  " + (room.isCommon() ? "yes" : "no"), new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				room.setCommon(!room.isCommon());
//				((TextView)view).setString("common room:  " + (room.isCommon() ? "yes" : "no"));
//			}
//		}, null);
//
//		// Owner
//		addEntry("set owner", new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				addSubMenu(1, subMenu);
//			}
//		}, null);
//
//		setVisible(true);
//	}
//
//}
