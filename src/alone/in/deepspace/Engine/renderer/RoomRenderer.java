package alone.in.deepspace.engine.renderer;

import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.util.Constant;

public class RoomRenderer implements IRenderer {
	private RenderTexture 	_cache;

	public RoomRenderer() {
		try {
			_cache = new RenderTexture();
			_cache.create(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
			_cache.display();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		
		Room room = new Room(Room.Type.METTING, 12, 12);
		ServiceManager.getWorldMap().addRoom(room);
	}


	public void onDraw(RenderWindow app, RenderStates render, double animProgress) {
		RectangleShape rectangleItem = new RectangleShape(new Vector2f(Constant.TILE_WIDTH, Constant.TILE_HEIGHT));
		
		Room[][] rooms = Game.getRoomManager().getRooms();
		for (int x = 0; x < Constant.WORLD_WIDTH; x++) {
			for (int y = 0; y < Constant.WORLD_HEIGHT; y++) {
				Room room = rooms[x][y];
				if (room != null) {
					if (x < room.getMinX()) {
						room.setMinX(x);
					}
					if (x > room.getMaxX()) {
						room.setMaxX(x);
					}
					Color color = room.getColor();
					rectangleItem.setFillColor(new Color(50 + color.r, 50 + color.g, 50 + color.b, 100));
					rectangleItem.setPosition(new Vector2f(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT));
					app.draw(rectangleItem, render);
				}
			}			
		}
		
		List<Room> roomList = Game.getRoomManager().getRoomList();
		for (Room room: roomList) {
			displayRoomInfo(app, render, room, room.getX() * Constant.TILE_WIDTH, room.getY() * Constant.TILE_HEIGHT);
		}
	}


	private void displayRoomInfo(RenderWindow app, RenderStates render, Room room, int x, int y) {
		int characterSize = 11;
		int characterSubSize = 8;
		int height = 20;
		int padding = 6;
		if (room.getWidth() >= 5) {
			characterSize = 32;
			characterSubSize = 16;
			padding = 10;
			height = 38;
		} else if (room.getWidth() >= 4) {
			characterSize = 30;
			characterSubSize = 14;
			height = 28;
			padding = 6;
		} else if (room.getWidth() >= 3) {
			characterSize = 20;
			characterSubSize = 10;
			height = 26;
			padding = 3;
		}				
				
		Text text = new Text();
		text.setFont(SpriteManager.getInstance().getFont());
		
		// Type
		text.setString(room.getName());
		text.setColor(room.getColor());
		text.setCharacterSize(characterSize);
		
		text.setPosition(x + padding - 1, y + 1);
		app.draw(text, render);
		text.setPosition(x + padding + 1, y + 1);
		app.draw(text, render);
		text.setPosition(x + padding - 1, y - 1);
		app.draw(text, render);
		text.setPosition(x + padding + 1, y - 1);
		app.draw(text, render);
		
//		text.setPosition(x + 11, y + 2);
//		text.setColor(room.getColor());
//		MainRenderer.getInstance().draw(text, render);

		text.setPosition(x + padding, y);
		text.setColor(Color.WHITE);
		app.draw(text, render);
		
		// Owner
		if (room.getOwner() != null) {
			text.setPosition(x + padding, y + height);
			text.setCharacterSize(characterSubSize);
			text.setString("(" + room.getOwner().getLastName() + ")");
			app.draw(text, render);
		}

	}


	@Override
	public void onRefresh(int frame) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void invalidate(int x, int y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

}