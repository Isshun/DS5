package alone.in.deepspace.engine.loader;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.engine.loader.WorldSaver.WorldSave;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.Character.Gender;
import alone.in.deepspace.model.character.CharacterRelation;

public class CharacterSerializer implements SerializerInterface {

	public static class CharacterRelationSave {
		public int	id;
		public int	relationId;
		
		public CharacterRelationSave(CharacterRelation relation) {
			this.id = relation.getSecond().getId();
			this.relationId = relation.getRelation().ordinal();
		}
	}
	
	public static class CharacterSave {
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
		List<Character> characters = ServiceManager.getCharacterManager().getList();
		save.characters = new ArrayList<CharacterSave>();

		for (Character character: characters) {
			save.characters.add(new CharacterSave(character));
		}
	}

	public void load(WorldSave save) {
	    for (CharacterSave characterSave: save.characters) {
			ServiceManager.getCharacterManager().add(loadCharacter(characterSave));
	    }		
	}

	private static Character loadCharacter(CharacterSave characterSave) {
		Character character = new Character(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
		character.setGender(characterSave.gender);
		return character;
	}

}
