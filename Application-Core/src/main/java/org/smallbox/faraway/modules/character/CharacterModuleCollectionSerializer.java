package org.smallbox.faraway.modules.character;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.GenericGameCollectionSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.modules.character.model.HumanModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;

@GameObject
public class CharacterModuleCollectionSerializer extends GenericGameCollectionSerializer<CharacterModel> {
    @Inject private Data data;
    @Inject private CharacterModule characterModule;

    @Override
    public int getModulePriority() { return Constant.MODULE_CHARACTER_PRIORITY; }

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE characters (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, firstname TEXT, lastname TEXT)");
    }

    @Override
    public void onSaveEntry(SQLiteConnection db, CharacterModel character) throws SQLiteException {
        SQLiteStatement st = db.prepare("INSERT INTO characters (_id, x, y, z, firstname, lastname) VALUES (?, ?, ?, ?, ?, ?)");
        try {
            st.bind(1, character.getId());
            st.bind(2, character.getParcel().x);
            st.bind(3, character.getParcel().y);
            st.bind(4, character.getParcel().z);
            st.bind(5, character.getExtra(CharacterPersonalsExtra.class).getFirstName());
            st.bind(6, character.getExtra(CharacterPersonalsExtra.class).getLastName());
            st.step();
        } finally {
            st.dispose();
        }
    }

    @Override
    public void onLoadEntry(SQLiteConnection db) throws SQLiteException {
        SQLiteStatement st = db.prepare("SELECT _id, x, y, z, firstname, lastname FROM characters");
        try {
            while (st.step()) {
                int id = st.columnInt(0);
                int x = st.columnInt(1);
                int y = st.columnInt(2);
                int z = st.columnInt(3);
                String firstname = st.columnString(4);
                String lastname =  st.columnString(5);

                CharacterInfo characterInfo = data.characters.get("base.character.human");
                characterModule.add(new HumanModel(id, characterInfo, WorldHelper.getParcel(x, y, z)));
            }
        } finally {
            st.dispose();
        }
    }

    @Override
    public Collection<CharacterModel> getEntries() {
        return characterModule.getAll();
    }

}