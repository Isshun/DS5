package org.smallbox.faraway.modules.plant.model;

import org.json.JSONObject;
import org.smallbox.faraway.core.ModelSerializer;

public class PlantSerializer implements ModelSerializer<PlantItem> {

    @Override
    public JSONObject serialize(PlantItem plant) {
        return new JSONObject()
                .put("id", plant._id)
                .put("parcelX", plant.getParcel().x)
                .put("parcelY", plant.getParcel().y)
                .put("parcelZ", plant.getParcel().z);
    }

}