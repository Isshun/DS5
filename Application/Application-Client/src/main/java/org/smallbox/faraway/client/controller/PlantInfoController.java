package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.flora.PlantModule;
import org.smallbox.faraway.modules.flora.model.PlantItem;

import java.util.List;
import java.util.OptionalInt;

/**
 * Created by Alex on 26/04/2016.
 */
public class PlantInfoController extends AbsInfoLuaController<PlantItem> {

    @BindModule
    private PlantModule plantModule;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbJob;

    @BindLua
    private UILabel lbMaturity;

    @BindLua
    private UILabel lbGarden;

    @BindLua
    private UILabel lbSeed;

    @BindLua
    private UILabel lbNourish;

    @BindLua
    private UILabel lbGrowing;

    @BindLua
    private UILabel lbMinTemperature;

    @BindLua
    private UILabel lbMaxTemperature;

    @BindLua
    private UILabel lbTemperature;

    @BindLua
    private View imgTemperature;

    @BindLua
    private View imgLight;

    @BindLua
    private View imgMoisture;

    @BindLua
    private View imgOxygen;

    @Override
    protected void onDisplayUnique(PlantItem plant) {
        lbLabel.setText(plant.getLabel());
        lbMaturity.setText("Maturity: " + plant.getMaturity());
        lbGarden.setText("Garden: " + plant.getGarden());
        lbSeed.setText("Seed: " + plant.hasSeed());
        lbNourish.setText("Nourish: " + plant.getNourish());
        lbGrowing.setText("Growing: " + (plant.getGrowingInfo() != null ? plant.getGrowingInfo().name : ""));

        ParcelModel parcel = plant.getParcel();
        if (parcel != null) {
            OptionalInt minTemperature = plant.getInfo().plant.states.stream().filter(value -> value.temperature != null).mapToInt(value -> value.temperature[0]).min();
            OptionalInt maxTemperature = plant.getInfo().plant.states.stream().filter(value -> value.temperature != null).mapToInt(value -> value.temperature[1]).max();
            if (minTemperature.isPresent() && maxTemperature.isPresent()) {
                int temperature = 12;
//                parcel.getTemperature();
                lbMinTemperature.setText(String.valueOf(minTemperature.getAsInt()));
                lbMaxTemperature.setText(String.valueOf(maxTemperature.getAsInt()));
                lbTemperature.setText(String.valueOf(temperature));

                int i1 = temperature - minTemperature.getAsInt();
                int i2 = maxTemperature.getAsInt() - minTemperature.getAsInt();
                imgTemperature.setPosition(i1 * 128 / i2, 22);
            }
        }
        imgLight.setPosition(100, 22);
        imgMoisture.setPosition(80, 22);
        imgOxygen.setPosition(50, 22);

        plant.getGrowingInfo();

//        listStates.clear();
//        plant.getInfo().plant.states.forEach(growingInfo -> {
//            int color = plant.getGrowingInfo() == growingInfo ? 0xB8E394 : 0xB4D4D3;
//
//            View view = new UIFrame(null);
//            view.setSize(300, 20);
//
//            view.addView(UILabel.create(null).setText(growingInfo.name).setTextColor(color).setSize(100, 20));
////            view.addView(UILabel.create(null).setText(growingInfo.temperature[0] + " to " + growingInfo.temperature[1]).setTextColor(color).setSize(100, 20).setPosition(100, 0));
////            view.addView(UILabel.create(null).setText(growingInfo.light[0] + " to " + growingInfo.light[1]).setTextColor(color).setSize(100, 20).setPosition(200, 0));
//
//            listStates.addView(view);
//        });

//        lbQuantity.setText(String.valueOf(plant.getGrowingInfo()));
    }

    @Override
    protected void onDisplayMultiple(List<PlantItem> list) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    protected PlantItem getObjectOnParcel(ParcelModel parcel) {
        return plantModule.getPlant(parcel);
    }
}
