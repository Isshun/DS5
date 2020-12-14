package org.smallbox.faraway.client;

import org.json.JSONObject;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.ModelDeserializer;

public class PlantCommonDeserializer implements ModelDeserializer<PlantCommon> {

    @Override
    public PlantCommon deserialize(JSONObject object) {
        long id = object.getLong("id");

        PlantCommon plant = new PlantCommon();
        plant.id = id;

        plant.parcelX = object.getInt("parcelX");
        plant.parcelY = object.getInt("parcelY");
        plant.parcelZ = object.getInt("parcelZ");

        return plant;
    }

}
