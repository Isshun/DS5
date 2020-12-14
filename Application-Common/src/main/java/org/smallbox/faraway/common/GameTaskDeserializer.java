package org.smallbox.faraway.common;

import org.json.JSONObject;

/**
 * Created by 300206 on 14/09/2017.
 */
public class GameTaskDeserializer implements ModelDeserializer<ClientGameTask> {

    @Override
    public ClientGameTask deserialize(JSONObject jsonObject) {
        ClientGameTask clientGameTask = new ClientGameTask();
        clientGameTask.id = jsonObject.getLong("id");
        clientGameTask.label = jsonObject.getString("label");
        clientGameTask.duration = jsonObject.getLong("duration");
        clientGameTask.elapsed = jsonObject.getLong("elapsed");
        return clientGameTask;
    }

}
