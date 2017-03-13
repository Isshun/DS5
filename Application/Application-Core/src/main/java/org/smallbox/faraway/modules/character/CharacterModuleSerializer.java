package org.smallbox.faraway.modules.character;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.modules.character.model.HumanModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

public class CharacterModuleSerializer extends GameSerializer<CharacterModule> {

    @Override
    public int getModulePriority() { return Constant.MODULE_CHARACTER_PRIORITY; }

    @Override
    public void onSave(CharacterModule module, Game game) {
        Application.sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE characters (id INTEGER, x INTEGER, y INTEGER, z INTEGER, firstname TEXT, lastname TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO characters (id, x, y, z, firstname, lastname) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (CharacterModel character: module.getCharacters()) {
                        st.bind(1, character.getId());
                        st.bind(2, character.getParcel().x);
                        st.bind(3, character.getParcel().y);
                        st.bind(4, character.getParcel().z);
                        st.bind(5, character.getExtra(CharacterPersonalsExtra.class).getFirstName());
                        st.bind(6, character.getExtra(CharacterPersonalsExtra.class).getLastName());
                        st.step();
                        st.reset(false);
                    }
                    db.exec("end transaction");
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(CharacterModule module, Game game) {
        Application.sqlManager.post(db -> {
            try {
                SQLiteStatement st = db.prepare("SELECT id, x, y, z, firstname, lastname FROM characters");
                try {
                    while (st.step()) {
                        int id = st.columnInt(0);
                        int x = st.columnInt(1);
                        int y = st.columnInt(2);
                        int z = st.columnInt(3);
                        String firstname = st.columnString(4);
                        String lastname =  st.columnString(5);

                        module.addCharacter(new HumanModel(id, WorldHelper.getParcel(x, y, z), firstname, lastname, 10));
                    }
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                Log.warning("Unable to read characters table: " + e.getMessage());
            }
        });
    }
}