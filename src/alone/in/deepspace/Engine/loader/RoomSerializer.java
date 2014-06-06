package alone.in.deepspace.engine.loader;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.loader.WorldSaver.WorldSave;
import alone.in.deepspace.manager.RoomSave;
import alone.in.deepspace.manager.RoomSave.RoomSaveArea;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.Character.Gender;
import alone.in.deepspace.model.character.CharacterRelation;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.room.GardenRoom;
import alone.in.deepspace.model.room.Room;

public class RoomSerializer implements SerializerInterface {

	public class CharacterRelationSave {
		public int	id;
		public int	relationId;
		
		public CharacterRelationSave(CharacterRelation relation) {
			this.id = relation.getSecond().getId();
			this.relationId = relation.getRelation().ordinal();
		}
	}
	
	public class CharacterSave {
		public int							id;
		public String						lastname;
		public String 						firstname;
		public List<CharacterRelationSave>	relations;
		public double 						old;
		public int 							x;
		public int 							y;
		public Gender						gender;
		
		public CharacterSave(Character character) {
			this.id = character.getId();
			this.old = character.getOld();
			this.x = character.getX();
			this.y = character.getY();
			this.gender = character.getGender();
			this.lastname = character.getLastName();
			this.firstname = character.getFirstName();
			this.relations = new ArrayList<CharacterRelationSave>();
			for (CharacterRelation relation: character.getRelations()) {
				this.relations.add(new CharacterRelationSave(relation));
			}
		}
	}
	
	public void save(WorldSave save) {
		List<Room> rooms = Game.getRoomManager().getRoomList();
		save.rooms = new ArrayList<RoomSave>();
		
		for (Room room: rooms) {
			RoomSave roomSave = new RoomSave();
			roomSave.type = room.getType().ordinal();
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
				for (Character character: room.getOccupants()) {
					roomSave.occupants.add(character.getId());
				}
			}

			save.rooms.add(roomSave);
		}
	}

	public void	load(WorldSave save) {
		List<Character> characters = Game.getCharacterManager().getList();
		Character character = null;
		if (characters.size() > 0) {
			character = characters.get((int)(Math.random() * characters.size()));
		}
		
		for (RoomSave roomSave: save.rooms) {
			for (RoomSaveArea roomSaveArea: roomSave.areas) {
				Room room = Game.getRoomManager().putRoom(roomSaveArea.x, roomSaveArea.y, roomSaveArea.x, roomSaveArea.y, roomSaveArea.x, roomSaveArea.y, Room.getType(roomSave.type), null);
				if (room.isGarden()) {
					ItemInfo info = Game.getData().getItemInfo(roomSave.culture);
					((GardenRoom)room).setCulture(info);
				}
			}
		}
	}
}
