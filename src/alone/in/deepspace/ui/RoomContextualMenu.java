package alone.in.deepspace.ui;

import java.util.List;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.room.Room;

public class RoomContextualMenu extends ContextualMenu {

	public RoomContextualMenu(RenderWindow app, int tileIndex, Vector2f pos, Vector2f size, Viewport viewport, final Room room) {
		super(app, tileIndex, pos, size, viewport);
		
		List<Character> characters = ServiceManager.getCharacterManager().getList();
		final ContextualMenu subMenu = new ContextualMenu(app, 0, new Vector2f(100, 0), new Vector2f(160, (characters.size() + 1) * ContextualMenu.LINE_HEIGHT + ContextualMenu.PADDING_V * 2), viewport);
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
