package alone.in.deepspace.engine.serializer;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.serializer.WorldSaver.WorldSave;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.Character.Gender;
import alone.in.deepspace.model.character.CharacterRelation;
import alone.in.deepspace.model.character.CharacterRelation.Relation;
import alone.in.deepspace.util.Log;

public class CharacterSerializer implements SerializerInterface {

	public static class CharacterRelationSave {
		public int			id;
		private Relation 	relation;
		
		public CharacterRelationSave(CharacterRelation relation) {
			this.id = relation.getSecond().getId();
			this.relation = relation.getRelation();
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
		private double 						nextChildAtOld;
		public int							nbChild;
		
		public CharacterSave(Character character) {
			this.id = character.getId();
			this.old = character.getOld();
			this.x = character.getX();
			this.y = character.getY();
			this.gender = character.getGender();
			this.lastname = character.getLastName();
			this.firstname = character.getFirstName();
			this.relations = new ArrayList<CharacterRelationSave>();
			this.nextChildAtOld = character.getNextChildAtOld();
			this.nbChild = character.getNbChild();
			for (CharacterRelation relation: character.getRelations()) {
				this.relations.add(new CharacterRelationSave(relation));
			}
		}
	}

	private List<Character> _characters;
	
	public void save(WorldSave save) {
		List<Character> characters = Game.getCharacterManager().getList();
		save.characters = new ArrayList<CharacterSave>();

		for (Character character: characters) {
			save.characters.add(new CharacterSave(character));
		}
	}

	public void load(WorldSave save) {
		_characters = new ArrayList<Character>();
	    for (CharacterSave characterSave: save.characters) {
			loadCharacter(characterSave);
	    }
	    for (CharacterSave characterSave: save.characters) {
			loadCharacterRelation(characterSave);
	    }
	}

	private void loadCharacterRelation(CharacterSave characterSave) {
		Character character = getCharacterById(characterSave.id);
		
		for (CharacterRelationSave relationSave: characterSave.relations) {
			if (relationSave.relation == Relation.MATE) {
				character.addMateRelation(getCharacterById(relationSave.id));
			} else {
				character.getRelations().add(new CharacterRelation(character, getCharacterById(relationSave.id), relationSave.relation));
			}
		}
		
		character.setNextChildAtOld(characterSave.nextChildAtOld);
		character.setNbChild(characterSave.nbChild);
	}

	private Character getCharacterById(int id) {
		for (Character character: _characters) {
			if (character.getId() == id) {
				return character;
			}
		}
		return null;
	}

	private void loadCharacter(CharacterSave characterSave) {
		Character character = new Character(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
		character.setGender(characterSave.gender);
		Game.getCharacterManager().add(character);
		_characters.add(character);
	}

}
