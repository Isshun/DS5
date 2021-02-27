package org.smallbox.faraway.game.character;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameSerializerPriority;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;
import org.smallbox.faraway.game.character.model.HumanModel;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.Collection;

@GameObject
public class CharacterModuleCollectionSerializer extends GenericGameCollectionSerializer<CharacterModel> {
    private final static String CREATE_TABLE_CMD = "CREATE TABLE characters (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, firstname TEXT, lastname TEXT)";
    private final static String INSERT_CMD = "INSERT INTO characters (_id, x, y, z, firstname, lastname) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SELECT_CMD = "SELECT _id, x, y, z, firstname, lastname FROM characters";

    @Inject private DataManager dataManager;
    @Inject private CharacterModule characterModule;

    public CharacterModuleCollectionSerializer() {
        super(CREATE_TABLE_CMD, INSERT_CMD, SELECT_CMD);
    }

    @Override
    public GameSerializerPriority getPriority() {
        return GameSerializerPriority.MODULE_CHARACTER_PRIORITY;
    }

    @Override
    public void onSaveEntry(SQLiteStatement statement, CharacterModel character) throws SQLiteException {
        statement.bind(1, character.getId());
        statement.bind(2, character.getParcel().x);
        statement.bind(3, character.getParcel().y);
        statement.bind(4, character.getParcel().z);
        statement.bind(5, character.getExtra(CharacterPersonalsExtra.class).getFirstName());
        statement.bind(6, character.getExtra(CharacterPersonalsExtra.class).getLastName());
    }

    @Override
    public void onLoadEntry(SQLiteStatement statement) throws SQLiteException {
        int id = statement.columnInt(0);
        int x = statement.columnInt(1);
        int y = statement.columnInt(2);
        int z = statement.columnInt(3);
        String firstname = statement.columnString(4);
        String lastname = statement.columnString(5);

        CharacterInfo characterInfo = dataManager.characters.get("base.character.human");
        characterModule.add(new HumanModel(id, characterInfo, WorldHelper.getParcel(x, y, z)));
    }

    @Override
    public Collection<CharacterModel> getEntries() {
        return characterModule.getAll();
    }

}