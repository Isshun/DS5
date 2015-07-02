//package org.smallbox.faraway.engine.renderer;
//
//import org.jsfml.graphics.RenderTexture;
//import org.jsfml.graphics.Text;
//import org.jsfml.graphics.TextureCreationException;
//import org.smallbox.faraway.engine.Color;
//import org.smallbox.faraway.engine.GFXRenderer;
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.engine.RenderEffect;
//import org.smallbox.faraway.ui.engine.ColorView;
//import org.smallbox.faraway.util.Constant;
//import org.smallbox.faraway.game.model.room.Room;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class RoomRenderer implements IRenderer {
//	private static class RoomEntry {
//		public final Room	room;
//
//		public RoomEntry(Room room) {
//			this.room = room;
//		}
//
//	}
//
//	private RenderTexture 			_cache;
//	private Map<Room, RoomEntry>	_roomEntries;
//
//	public RoomRenderer() {
//		_roomEntries = new HashMap<Room, RoomEntry>();
//
//		try {
//			_cache = new RenderTexture();
//			_cache.onCreate(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
//			_cache.display();
//		} catch (TextureCreationException e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	public void onDraw(GFXRenderer renderer, RenderEffect buffEffect, double animProgress) {
//		ColorView rectangleItem = new ColorView(Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
//
//		Room[][] rooms = Game.getRoomManager().getRooms();
//		for (int x = 0; x < Constant.WORLD_WIDTH; x++) {
//			for (int y = 0; y < Constant.WORLD_HEIGHT; y++) {
//				Room room = rooms[x][y];
//				if (room != null) {
//					if (x < room.getMinX()) {
//						room.setMinX(x);
//					}
//					if (x > room.getMaxX()) {
//						room.setMaxX(x);
//					}
//					Color color = room.getColor();
//					rectangleItem.setBackgroundColor(new Color(50 + color.r, 50 + color.g, 50 + color.b, 100));
//					rectangleItem.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
//					renderer.draw(rectangleItem, buffEffect);
//				}
//			}
//		}
//
//		List<Room> roomList = Game.getRoomManager().getRoomList();
//		for (Room room: roomList) {
//			displayRoomInfo(renderer, buffEffect, room, room.getX() * Constant.TILE_WIDTH, room.getY() * Constant.TILE_HEIGHT);
//		}
//	}
//
//
//	private void displayRoomInfo(GFXRenderer renderer, RenderEffect buffEffect, Room room, int x, int y) {
//		int characterSize = 11;
//		int characterSubSize = 8;
//		int height = 20;
//		int padding = 6;
//		if (room.getWidth() >= 5) {
//			characterSize = 32;
//			characterSubSize = 16;
//			padding = 10;
//			height = 38;
//		} else if (room.getWidth() >= 4) {
//			characterSize = 30;
//			characterSubSize = 14;
//			height = 28;
//			padding = 6;
//		} else if (room.getWidth() >= 3) {
//			characterSize = 20;
//			characterSubSize = 10;
//			height = 26;
//			padding = 3;
//		}
//
//		RoomEntry entry = new RoomEntry(room);
//		_roomEntries.put(room, entry);
//
//		Text text = new Text();
//		text.setFont(SpriteManager.getInstance().getFont());
//
//		// Type
//		text.setString(room.getName());
//		//text.setColor(room.getColor());
//		text.setCharacterSize(characterSize);
//
//		text.setPosition(x + padding - 1, y + 1);
//		renderer.draw(text, buffEffect);
//		text.setPosition(x + padding + 1, y + 1);
//		renderer.draw(text, buffEffect);
//		text.setPosition(x + padding - 1, y - 1);
//		renderer.draw(text, buffEffect);
//		text.setPosition(x + padding + 1, y - 1);
//		renderer.draw(text, buffEffect);
//
//		text.setPosition(x + padding, y);
//		//text.setColor(Color.WHITE);
//		renderer.draw(text, buffEffect);
//
//		// Owner
//		if (room.getOwner() != null) {
//			text.setPosition(x + padding, y + height);
//			text.setCharacterSize(characterSubSize);
//			text.setString("(" + room.getOwner().getLastName() + ")");
//			renderer.draw(text, buffEffect);
//		}
//
//	}
//
//
//	@Override
//	public void onDraw(int frame) {
////		List<Room> roomList = Game.getRoomManager().getRoomList();
////		for (Room room: roomList) {
////			displayRoomInfo(app, _renderEffect, room, room.getX() * Constant.TILE_WIDTH, room.getY() * Constant.TILE_HEIGHT);
////		}
//	}
//
//
//	@Override
//	public void invalidate(int x, int y) {
//		// TODO Auto-generated method stub
//
//	}
//
//
//	@Override
//	public void invalidate() {
//		// TODO Auto-generated method stub
//
//	}
//
//}