package org.smallbox.faraway.data.serializer;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterInfoModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterNeeds;
import org.smallbox.faraway.game.model.character.base.CharacterRelation;
import org.smallbox.faraway.game.model.character.base.CharacterRelation.Relation;
import org.smallbox.faraway.util.Utils;

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
		public double	joy;

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
			this.joy = needs.joy;
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
        public final String                 type;
		public double 						old;
		public int 							x;
		public int 							y;
		public CharacterInfoModel.Gender 	gender;
		private double 						nextChildAtOld;
		public int							nbChild;
		public CharacterNeedsSave 			needs;
		private ArrayList<String> 			inventory;
		
		public CharacterSave(CharacterModel character) {
			this.id = character.getId();
			this.old = character.getOld();
			this.x = character.getX();
			this.y = character.getY();
			this.type = character.getTypeName();
			this.gender = character.getInfo().getGender();
			this.lastname = character.getInfo().getLastName();
			this.firstname = character.getInfo().getFirstName();
			this.relations = new ArrayList<>();
			this.nextChildAtOld = character.getRelations().getNextChildAtOld();
			this.nbChild = character.getRelations().getNbChild();
			this.inventory = new ArrayList<String>();
//			for (MapObjectModel item: character.getInventory()) {
//				this.inventory.add(item.getInfo().name);
//			}
			this.needs = new CharacterNeedsSave(character.getNeeds());
			for (CharacterRelation relation: character.getRelations().getRelations()) {
				this.relations.add(new CharacterRelationSave(relation));
			}
		}
	}

	private List<CharacterModel> _characters;
	
	public void save(GameSerializer.GameSave save) {
		save.characters = Game.getCharacterManager().getCharacters().stream().map(CharacterSave::new).collect(Collectors.toList());
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
				character.getRelations().addMateRelation(character, getCharacterById(relationSave.id));
			} else {
				character.getRelations().getRelations().add(new CharacterRelation(character, getCharacterById(relationSave.id), relationSave.relation));
			}
		}
		
		character.getRelations().setNextChildAtOld(characterSave.nextChildAtOld);
		character.getRelations().setNbChild(characterSave.nbChild);
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

		CharacterModel character;

        switch (characterSave.type) {
            case "android":
                character = new AndroidModel(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
                break;
            case "droid":
                character = new DroidModel(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
                break;
            default:
                character = new HumanModel(characterSave.id, characterSave.x, characterSave.y, characterSave.firstname, characterSave.lastname, characterSave.old);
                break;
        }

		if (characterSave.gender != null) {
			character.getInfo().setGender(characterSave.gender);
		}

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
			character.getNeeds().joy = characterSave.needs.joy;
		}
		Game.getCharacterManager().add(character);
		_characters.add(character);
	}

}
