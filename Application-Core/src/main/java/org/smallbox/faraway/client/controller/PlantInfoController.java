package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameAction.OnGameSelectAction;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.world.Parcel;

import java.util.Queue;

@GameObject
public class PlantInfoController extends AbsInfoLuaController<PlantItem> {
    @Inject private PlantModule plantModule;

    @BindLua private UILabel lbName;
    @BindLua private UILabel lbJob;
    @BindLua private UILabel lbMaturity;
    @BindLua private UILabel lbGarden;
    @BindLua private UILabel lbSeed;
    @BindLua private UILabel lbNourish;
    @BindLua private UILabel lbGrowing;
    @BindLua private UILabel lbTemperature;
    @BindLua private UILabel lbLight;
    @BindLua private UILabel lbMoisture;
    @BindLua private UILabel lbOxygen;
    @BindLua private UILabel lbCurrentTemperature;
    @BindLua private UILabel lbCurrentOxygen;
    @BindLua private UILabel lbCurrentMoisture;
    @BindLua private UILabel lbCurrentLight;
    @BindLua private View imgTemperature;
    @BindLua private View imgLight;
    @BindLua private View imgMoisture;
    @BindLua private View imgOxygen;

    private PlantItem plant;

    @OnGameSelectAction(PlantItem.class)
    protected void onSelectPlant(PlantItem plant) {
        this.plant = plant;
        setVisible(true);
        displayPlant();
    }

    @OnGameUpdate(runOnMainThread = true)
    public void onGameUpdate() {
        displayPlant();
    }

    private void displayPlant() {
        if (plant != null) {
            lbName.setText(plant.getLabel());
            lbMaturity.setText("Maturity: " + (int)(plant.getMaturity() * 100) + "%");
            lbGarden.setText("Garden: " + plant.getGarden());
            lbSeed.setText("Seed: " + plant.hasSeed());
            lbNourish.setText("Nourish: " + plant.getNourish());
            lbGrowing.setText("Growing: " + (plant.getGrowingInfo() != null ? plant.getGrowingInfo().name : ""));

            double minTemperature = plant.getInfo().plant.temperature.min;
            double maxTemperature = plant.getInfo().plant.temperature.max;
            double bestTemperature = plant.getInfo().plant.temperature.best;
            lbTemperature.setText(minTemperature + " > " + bestTemperature + " > " + maxTemperature);

            double minLight = 0;
            double maxLight = 1;
            double bestLight = 0.8;
            lbLight.setText(minLight + " > " + bestLight + " > " + maxLight);

            double minMoisture = 0;
            double maxMoisture = 1;
            double bestMoisture = 0.5;
            lbMoisture.setText(minMoisture + " > " + bestMoisture + " > " + maxMoisture);

            double minOxygen = 0;
            double maxOxygen = 1;
            double bestOxygen = 0.5;
            lbOxygen.setText(minOxygen + " > " + bestOxygen + " > " + maxOxygen);

            Parcel parcel = plant.getParcel();
            if (parcel != null) {

                // Temperature
                int positionTemperature = getStatePosition(parcel.getTemperature(), bestTemperature, minTemperature, maxTemperature);
                imgTemperature.setPosition(positionTemperature - 4, 16);
                lbCurrentTemperature.setPosition(positionTemperature - 8, 32);
                lbCurrentTemperature.setText(parcel.getTemperature() + "°");

                // Light
                int positionLight = getStatePosition(parcel.getLight(), bestLight, minLight, maxLight);
                imgLight.setPosition(positionLight - 4, 16);
                lbCurrentLight.setPosition(positionLight - 8, 32);
                lbCurrentLight.setText((int)(parcel.getLight() * 100) + "%");

                // Moisture
                int positionMoisture = getStatePosition(parcel.getMoisture(), bestMoisture, minMoisture, maxMoisture);
                imgMoisture.setPosition(positionMoisture - 4, 16);
                lbCurrentMoisture.setPosition(positionMoisture - 8, 32);
                lbCurrentMoisture.setText((int)(parcel.getMoisture() * 100) + "%");


                // Oxygen
                int positionOxygen = getStatePosition(parcel.getOxygen(), bestOxygen, minOxygen, maxOxygen);
                imgOxygen.setPosition(positionOxygen - 4, 16);
                lbCurrentOxygen.setPosition(positionOxygen - 8, 32);
                lbCurrentOxygen.setText((int)(parcel.getOxygen() * 100) + "%");
            }
        }
    }

    private int getStatePosition(double currentTemperature, double bestTemperature, double minTemperature, double maxTemperature) {
        return currentTemperature < bestTemperature
                ? (int) ((currentTemperature - minTemperature) * 64 / (bestTemperature - minTemperature))
                : 128 - (int) ((currentTemperature - maxTemperature) * 64 / (bestTemperature - maxTemperature));
    }

    @Override
    protected void onDisplayUnique(PlantItem plantItem) {

    }

    @Override
    protected void onDisplayMultiple(Queue<PlantItem> objects) {
    }

    @Override
    public PlantItem getObjectOnParcel(Parcel parcel) {
        return plantModule.getPlant(parcel);
    }
}
