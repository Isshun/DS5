package org.smallbox.faraway.core.game.module.character;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.HumanModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class CharacterModuleSerializer extends SerializerInterface {

    @Override
    public int getModulePriority() { return Constant.MODULE_CHARACTER_PRIORITY; }

    @Override
    public void save() {
        SQLHelper.getInstance().post(db -> {
            try {
                db.exec("CREATE TABLE characters (id INTEGER, x INTEGER, y INTEGER, z INTEGER, firstname TEXT, lastname TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO characters (id, x, y, z, firstname, lastname) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {
                        st.bind(1, character.getId());
                        st.bind(2, character.getParcel().x);
                        st.bind(3, character.getParcel().y);
                        st.bind(4, character.getParcel().z);
                        st.bind(5, character.getPersonals().getFirstName());
                        st.bind(6, character.getPersonals().getLastName());
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

    public void load(Game game) {
        SQLHelper.getInstance().post(db -> {
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

                        CharacterModel character = new HumanModel(id, WorldHelper.getParcel(x, y, z), firstname, lastname, 10);
                        ModuleHelper.getCharacterModule().add(character);
                    }
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }
}