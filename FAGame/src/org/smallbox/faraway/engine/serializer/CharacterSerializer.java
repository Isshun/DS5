package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.character.AndroidModel;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.character.base.CharacterModel.Gender;
import org.smallbox.faraway.model.character.base.CharacterNeeds;
import org.smallbox.faraway.model.character.base.CharacterRelation;
import org.smallbox.faraway.model.character.base.CharacterRelation.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterSerializer implements SerializerInterface {

	public static class CharacterNeedsSave {
		// Actions
		public boolean	isSleeping;

		// Stats
		public double 	drinking;
		public double 	socialize;
		public double	food;
		public double 	happiness;
		public double	relation;
		public double	security;
		public double	oxygen;
		public double	energy;
		public double	health;
		public double	sickness;
		public double	injuries;
		public double	satiety;
		
		public CharacterNeedsSave(CharacterNeeds needs) {
			this.isSleeping = needs.isSleeping();
			this.drinking = needs.drinking;
			this.socialize = needs.socialize;
			this.food = needs.getFood();
			this.happiness = needs.getHappiness();
			this.security = needs.security;
			this.oxygen = needs.oxygen;
			this.energy = needs.energy;
			this.health = needs.health;
			this.sickness = needs.sickness;
			this.injuries = needs.injuries;
			this.satiety = needs.satiety;
		}
	}

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
		public CharacterNeedsSave 			needs;
		private ArrayList<String> 			inventory;
		
		public CharacterSave(CharacterModel character) {
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
			this.inventory = new ArrayList<String>();
//			for (MapObjectModel item: character.getInventory()) {
//				this.inventory.add(item.getInfo().name);
//			}
			this.needs = new CharacterNeedsSave(character.getNeeds());
			for (CharacterRelation relation: character.getRelations()) {
				this.relations.add(new CharacterRelationSave(relation));
			}
		}
	}

	private List<CharacterModel> _characters;
	
	public void save(GameSerializer.GameSave save) {
		List<CharacterModel> characters = Game.getCharacterManager().getList();
		save.characters = characters.stream().map(CharacterSave::new).collect(Collectors.toList());
	}

	public void load(GameSerializer.GameSave save) {
		_characters = new ArrayList<>();
		save.characters.forEach(this::loadCharacter);
		save.characters.forEach(this::loadCharacterRelation);
	}

	private void loadCharacterRelation(CharacterSave characterSave) {
		CharacterModel character = getCharacterById(characterSave.id);
		
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

	private CharacterModel getCharacterById(int id) {
		for (CharacterModel character: _characters) {
			if (character.getId() == id) {
				return character;
			}
		}
		return null;
	}

	private void loadCharacter(CharacterSave characterSave) {
		Utils.useUUID(characterSave.id);
		CharacterModel character = new AndroidModel(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
		character.setGender(characterSave.gender);

//		// Load inventory
//		if (characterSave.inventory != null) {
//			for (String name: characterSave.inventory) {
//				ItemInfo info = Game.getData().getItemInfo(name);
//				character.addInventory(new ConsumableModel(info));
//			}
//		}
		
		// Load needs
		if (characterSave.needs != null) {
			character.getNeeds().drinking = characterSave.needs.drinking;
			character.getNeeds().energy = characterSave.needs.energy;
			character.getNeeds().food = characterSave.needs.food;
			character.getNeeds().happiness = characterSave.needs.happiness;
			character.getNeeds().health = characterSave.needs.health;
			character.getNeeds().injuries = characterSave.needs.injuries;
			character.getNeeds().oxygen = characterSave.needs.oxygen;
			character.getNeeds().relation = characterSave.needs.relation;
			character.getNeeds().satiety = characterSave.needs.satiety;
			character.getNeeds().security = characterSave.needs.security;
			character.getNeeds().sickness = characterSave.needs.sickness;
			character.getNeeds().setSleeping(characterSave.needs.isSleeping);
			character.getNeeds().socialize = characterSave.needs.socialize;
		}
		Game.getCharacterManager().add(character);
		_characters.add(character);
	}

}
