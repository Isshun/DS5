package org.smallbox.faraway.modules.character;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.modules.character.model.HumanModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.util.Constant;

@GameObject
public class CharacterModuleSerializer extends GameSerializer {

    @Inject
    private Game game;

    @Inject
    private Data data;

    @Inject
    private CharacterModule module;

    @Override
    public int getModulePriority() { return Constant.MODULE_CHARACTER_PRIORITY; }

    @Override
    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE characters (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, firstname TEXT, lastname TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO characters (_id, x, y, z, firstname, lastname) VALUES (?, ?, ?, ?, ?, ?)");
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
                throw new GameException(CharacterModuleSerializer.class, "Error during save");
            }
        });
    }

    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
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
                        module.add(new HumanModel(id, characterInfo, WorldHelper.getParcel(x, y, z)));
                    }
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(CharacterModuleSerializer.class, "Error during load");
            }
        });
    }
}