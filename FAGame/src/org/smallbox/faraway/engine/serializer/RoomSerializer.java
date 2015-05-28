package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.serializer.WorldSaver.WorldSave;
import org.smallbox.faraway.manager.CharacterManager;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldArea;
import org.smallbox.faraway.model.room.GardenRoom;
import org.smallbox.faraway.model.room.QuarterRoom;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.model.room.Room.Type;
import org.smallbox.faraway.model.room.StorageRoom;

import java.util.ArrayList;
import java.util.List;

public class RoomSerializer implements SerializerInterface {
	public static class RoomSaveArea {
		public int 				x;
		public int 				y;
		
		public RoomSaveArea(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static class RoomSave {
		public Type 				type;
		public String				culture;
		public ArrayList<Integer> 	occupants;
		public List<RoomSaveArea>	areas;
		public int 					id;
		
		public RoomSave() {
			this.areas = new ArrayList<RoomSaveArea>();
		}
	}

	public void save(WorldSave save) {
		List<Room> rooms = Game.getRoomManager().getRoomList();
		save.rooms = new ArrayList<RoomSave>();
		
		for (Room room: rooms) {
			RoomSave roomSave = new RoomSave();
			roomSave.id = room.getId();
			roomSave.type = room.getType();
			for (WorldArea area: room.getAreas()) {
				roomSave.areas.add(new RoomSaveArea(area.getX(), area.getY()));
			}

			// Garden
			if (room.isGarden()) {
				GardenRoom garden = (GardenRoom)room;
				roomSave.culture = garden.getCulture().name;
			}
			
			// Quarter
			else if (room.isQuarter()) {
				roomSave.occupants = new ArrayList<Integer>();
				for (CharacterModel character: room.getOccupants()) {
					roomSave.occupants.add(character.getId());
				}
			}

			save.rooms.add(roomSave);
		}
	}

	public void	load(WorldSave save) {
		CharacterManager characterManager = Game.getCharacterManager();
		WorldManager worldManager = Game.getWorldManager();
		
		for (RoomSave roomSave: save.rooms) {
			Room room = null;
			Utils.useUUID(roomSave.id);
			
			// Is Garden
			if (roomSave.type == Type.GARDEN) {
				GardenRoom gardenRoom = new GardenRoom(roomSave.id);
				ItemInfo info = Game.getData().getItemInfo(roomSave.culture);
				gardenRoom.setCulture(info);
				room = gardenRoom;
			}
			
			// Is storage
			else if (roomSave.type == Type.STORAGE) {
				StorageRoom storageRoom = new StorageRoom(roomSave.id);
				room = storageRoom;
			}

			// Is quarter
			else if (roomSave.type == Type.QUARTER) {
				QuarterRoom quarterRoom = new QuarterRoom(roomSave.id);
				room = quarterRoom;
			}
			
			// Regular room
			else {
				room = new Room(roomSave.id, roomSave.type);
			}
			
			// Add occupants
			if (roomSave.occupants != null) {
				for (int characterId: roomSave.occupants) {
					room.addOccupant(characterManager.getCharacter(characterId));
				}
			}
			
			// Set areas
			for (RoomSaveArea area: roomSave.areas) {
				room.addArea(worldManager.getArea(area.x, area.y));
			}
			
			// Refresh position
			room.refreshPosition();
			
			// Is storage / second pass
			if (room.isStorage()) {
				StorageRoom storageRoom = (StorageRoom)room;
				
				// Get item on floor
				List<UserItem> items = new ArrayList<UserItem>();
				for (RoomSaveArea saveArea: roomSave.areas) {
					UserItem item = worldManager.takeItem(saveArea.x, saveArea.y);
					if (item != null) {
						items.add(item);
					}
				}
				
				// Add to storage
				for (UserItem item: items) {
					if (item.isStack()) {
						storageRoom.store(item);
					} else {
						storageRoom.store(item);
					}
				}
			}

			
			Game.getRoomManager().add(room);
		}
	}
}
