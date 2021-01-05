package org.smallbox.faraway.core.game.save;

import org.json.JSONObject;
import org.smallbox.faraway.core.GameException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameSaveInfo {
    public GameInfo game;
    public GameSaveType type;
    public String filename;
    public String label;
    public Date date;
    public int crew;
    public long duration;

    public JSONObject toJSON() {
        JSONObject saveJson = new JSONObject();
        saveJson.put("type", type.toString());
        saveJson.put("filename", filename);
        saveJson.put("label", label);
        saveJson.put("crew", crew);
        saveJson.put("duration", duration);
        saveJson.put("date", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).format(date));
        return saveJson;
    }


    public static GameSaveInfo fromJSON(JSONObject jsonSave) {
        GameSaveInfo saveInfo = new GameSaveInfo();
        saveInfo.type = GameSaveType.valueOf(jsonSave.getString("type").toUpperCase());
        saveInfo.filename = jsonSave.getString("filename");
        saveInfo.crew = jsonSave.optInt("crew", 0);
        saveInfo.duration = jsonSave.optInt("duration", 0);
        try {
            saveInfo.date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).parse(jsonSave.getString("date"));
            saveInfo.label = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.ENGLISH).format(saveInfo.date);
        } catch (ParseException e) {
            throw new GameException(GameInfo.class, "Cannot read GameInfo json");
        }
        return saveInfo;
    }

}
