package org.smallbox.faraway.core.game.module.character.model;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;

/**
 * Created by Alex on 02/07/2015.
 */
public class DiseaseInfo extends ObjectInfo {
    private DiseaseListener _listener;

    public interface DiseaseListener {
        void onStart(DiseaseCharacterModel data);
        void onUpdate(DiseaseCharacterModel data, int update);
    }

    public String       label;
    public String       message;
    public int          level;
    public LuaValue     data;
    public Globals      globals;
    public LuaValue     luaCharacter;
    private boolean     _visible;

    public void setListener(DiseaseListener listener) { _listener = listener; }
    public void setVisible(boolean visible) { _visible = visible; }

    public void start(DiseaseCharacterModel data) { _listener.onStart(data); }
    public void update(DiseaseCharacterModel data, int update) { _listener.onUpdate(data, update); }
}
