package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.serializer.WorldSaver.WorldSave;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.character.CharacterModel.Gender;
import org.smallbox.faraway.model.character.CharacterNeeds;
import org.smallbox.faraway.model.character.CharacterRelation;
import org.smallbox.faraway.model.character.CharacterRelation.Relation;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.UserItem;

import java.util.ArrayList;
import java.util.List;

public class CharacterSerializer implements SerializerInterface {

	public static class CharacterNeedsSave {
		// Actions
		public int	sleeping;
		public int	eating;
		public int	drinking;
		public int	socialize;

		// Stats
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
			this.sleeping = needs.getSleeping();
			this.eating = needs.getEating();
			this.drinking = needs.getDrinking();
			this.socialize = needs.getSocialize();
			
			this.food = needs.getFood();
			this.happiness = needs.getHappiness();
			this.security = needs.getSecurity();
			this.oxygen = needs.getOxygen();
			this.energy = needs.getEnergy();
			this.health = needs.getHealth();
			this.sickness = needs.getSickness();
			this.injuries = needs.getInjuries();
			this.satiety = needs.getSatiety();
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
		private ArrayList<String> inventory;
		
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
			for (ItemBase item: character.getInventory()) {
				this.inventory.add(item.getInfo().name);
			}
			this.needs = new CharacterNeedsSave(character.getNeeds());
			for (CharacterRelation relation: character.getRelations()) {
				this.relations.add(new CharacterRelationSave(relation));
			}
		}
	}

	private List<CharacterModel> _characters;
	
	public void save(WorldSave save) {
		List<CharacterModel> characters = Game.getCharacterManager().getList();
		save.characters = new ArrayList<CharacterSave>();

		for (CharacterModel character: characters) {
			save.characters.add(new CharacterSave(character));
		}
	}

	public void load(WorldSave save) {
		_characters = new ArrayList<CharacterModel>();
	    for (CharacterSave characterSave: save.characters) {
			loadCharacter(characterSave);
	    }
	    for (CharacterSave characterSave: save.characters) {
			loadCharacterRelation(characterSave);
	    }
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
		CharacterModel character = new CharacterModel(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
		character.setGender(characterSave.gender);

		// Load inventory
		if (characterSave.inventory != null) {
			for (String name: characterSave.inventory) {
				ItemInfo info = Game.getData().getItemInfo(name);
				character.addInventory(new UserItem(info));
			}
		}
		
		// Load needs
		if (characterSave.needs != null) {
			character.getNeeds().setDrinking(characterSave.needs.drinking);
			character.getNeeds().setEating(characterSave.needs.eating);
			character.getNeeds().setEnergy(characterSave.needs.energy);
			character.getNeeds().setFood(characterSave.needs.food);
			character.getNeeds().setHapiness(characterSave.needs.happiness);
			character.getNeeds().setHealth(characterSave.needs.health);
			character.getNeeds().setInjuries(characterSave.needs.injuries);
			character.getNeeds().setOxygen(characterSave.needs.oxygen);
			character.getNeeds().setRelation(characterSave.needs.relation);
			character.getNeeds().setSatiety(characterSave.needs.satiety);
			character.getNeeds().setSecurity(characterSave.needs.security);
			character.getNeeds().setSickness(characterSave.needs.sickness);
			character.getNeeds().setSleeping(characterSave.needs.sleeping);
			character.getNeeds().setSocialize(characterSave.needs.socialize);
		}
		Game.getCharacterManager().add(character);
		_characters.add(character);
	}

}
