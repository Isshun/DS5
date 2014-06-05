package alone.in.deepspace.engine.loader;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.engine.loader.WorldSaver.WorldSave;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.RoomSave;
import alone.in.deepspace.manager.ServiceManager;
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
		List<Room> rooms = RoomManager.getInstance().getRoomList();
		save.rooms = new ArrayList<RoomSave>();
		
		for (Room room: rooms) {
			if (room.isGarden()) {
				GardenRoom garden = (GardenRoom)room;
				for (WorldArea area: room.getAreas()) {
					RoomSave roomSave = new RoomSave();
					roomSave.x = area.getX();
					roomSave.y = area.getY();
					roomSave.culture = garden.getCulture().name;
					roomSave.type = room.getType().ordinal();
					save.rooms.add(roomSave);
				}
			}
			if (room.isQuarter()) {
				for (WorldArea area: room.getAreas()) {
					RoomSave roomSave = new RoomSave();
					roomSave.x = area.getX();
					roomSave.y = area.getY();
					roomSave.occupants = new ArrayList<Integer>();
					for (Character character: room.getOccupants()) {
						roomSave.occupants.add(character.getId());
					}
					roomSave.type = room.getType().ordinal();
					save.rooms.add(roomSave);
				}
			}
		}
	}

	public void	load(WorldSave save) {
		List<Character> characters = ServiceManager.getCharacterManager().getList();
		Character character = null;
		if (characters.size() > 0) {
			character = characters.get((int)(Math.random() * characters.size()));
		}
		
		for (RoomSave roomSave: save.rooms) {
			Room room = RoomManager.getInstance().putRoom(roomSave.x, roomSave.y, roomSave.x, roomSave.y, roomSave.x, roomSave.y, Room.getType(roomSave.type), null);
			if (room.isGarden()) {
				ItemInfo info = ServiceManager.getData().getItemInfo(roomSave.culture);
				((GardenRoom)room).setCulture(info);
			}
		}
	}
}
